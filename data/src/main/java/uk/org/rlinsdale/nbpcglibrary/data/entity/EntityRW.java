/*
 * Copyright (C) 2014-2015 Richard Linsdale (richard.linsdale at blueyonder.co.uk).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.org.rlinsdale.nbpcglibrary.data.entity;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.actions.AbstractSavable;
import uk.org.rlinsdale.nbpcglibrary.common.Event;
import uk.org.rlinsdale.nbpcglibrary.api.HasInstanceDescription;
import uk.org.rlinsdale.nbpcglibrary.common.Listener;
import uk.org.rlinsdale.nbpcglibrary.common.LogBuilder;
import uk.org.rlinsdale.nbpcglibrary.common.LogicException;
import uk.org.rlinsdale.nbpcglibrary.data.LibraryOnStop;
import uk.org.rlinsdale.nbpcglibrary.api.EntityPersistenceManager;
import uk.org.rlinsdale.nbpcglibrary.data.dbfields.DBFieldsRW;
import uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState.DBENTITY;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState.NEW;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState.REMOVED;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityStateChange.REMOVE;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityStateChange.SAVE;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityFieldChangeEventParams.CommonEntityField.ALL;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityFieldChangeEventParams.CommonEntityField.ID;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState.NEWEDITING;
import uk.org.rlinsdale.nbpcglibrary.data.entityreferences.IdChangeEventParams;

/**
 * The abstract class defining an editable Entity.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 * @param <E> the entity class
 * @param <P> the Parent Entity Class
 * @param <F> the Fields enum class
 */
public abstract class EntityRW<E extends EntityRW, P extends Entity, F> extends EntityRO<F> {

    private final DBFieldsRW<E> dbfields;
    private final EntityPersistenceManager entityPersistenceManager;
    private final Event<IdChangeEventParams> idChangeEvent;
    private final EntityManagerRW<E, P> em;
    private final String entityname;
    private final EntityStateChangeListener entitystatechangelistener;
    private final EntitySavable savable = new EntitySavable();

    /**
     * Constructor.
     *
     * @param entityname the entity name
     * @param icon name of the icon graphic
     * @param id the entity Id
     * @param em the entity manager for this entity class
     * @param dbfields the entity fields
     */
    public EntityRW(String entityname, String icon, int id, EntityManagerRW<E, P> em, DBFieldsRW<E> dbfields) {
        this(entityname, icon, id, em, em.getEntityPersistenceManager(), dbfields);
    }

    private EntityRW(String entityname, String icon, int id, EntityManagerRW<E, P> em, EntityPersistenceManager entityPersistenceManager, DBFieldsRW<E> dbfields) {
        super(entityname, icon, id, entityPersistenceManager, dbfields);
        this.em = em;
        this.entityPersistenceManager = entityPersistenceManager;
        this.dbfields = dbfields;
        idChangeEvent = new Event<>(entityname);
        this.entityname = entityname;
        entitystatechangelistener = new EntityStateChangeListener("entity:" + instanceDescription());
        addStateListener(entitystatechangelistener);
        // and as this is new it is saveable (too early to rely on listener)
        savable.add();
    }

    /**
     * Add an Id Listener to this entity.
     *
     * @param listener the listener
     */
    public final void addIdListener(Listener<IdChangeEventParams> listener) {
        idChangeEvent.addListener(listener);
    }

    /**
     * Remove an Id listener from this entity.
     *
     * @param listener the listener
     */
    public final void removeIdListener(Listener<IdChangeEventParams> listener) {
        idChangeEvent.removeListener(listener);
    }

    /**
     * Add an Id Listener to this entity.
     *
     * @param listener the listener
     * @param mode the indicators of listener action (on current thread or on
     * event queue; priority/normal)
     */
    public final void addIdListener(Listener<IdChangeEventParams> listener, Event.ListenerMode mode) {
        idChangeEvent.addListener(listener, mode);
    }

    final void updateId(int id) {
        setId(id);
        fireFieldChange(ID);
    }

    /**
     * Save this entity to entity storage.
     *
     * @return true if save is successful
     */
    public boolean save() {
        try {
            JsonObjectBuilder job = Json.createObjectBuilder();
            EntityState oldState = getState();
            switch (oldState) {
                case DBENTITY:
                    return true; //we don't need to do anything as this is a straight copy of a db entity
                case REMOVED:
                    return false;
                case NEW:
                case NEWEDITING:
                    if (!checkRules()) {
                        return false;
                    }
                    _values(job);
                    dbfields.values(job);
                    em.persistTransient((E) this, entityPersistenceManager.insert(job.build()));
                    idChangeEvent.fire(new IdChangeEventParams());
                    setState(DBENTITY);
                    break;
                case DBENTITYEDITING:
                    if (!checkRules()) {
                        return false;
                    }
                    _diffs(job);
                    dbfields.diffs(job);
                    JsonObject jo = job.build();
                    if (!jo.isEmpty()) {
                        entityPersistenceManager.update(getId(), jo);
                    }
                    setState(DBENTITY);
                    break;
                default:
                    if (!checkRules()) {
                        return false;
                    }
            }
            fireStateChange(SAVE, oldState, DBENTITY);
            return true;
        } catch (IOException | LogicException ex) {
            return false;
        }
    }

    /**
     * Add all field values to the given map.
     *
     * @param job a JasonObjectBuilder into which field names (keys) and field
     * values are to be inserted.
     * @throws IOException if problem obtaining / parsing data
     */
    abstract protected void _values(JsonObjectBuilder job) throws IOException;

    /**
     * Add any modified field values to the given map.
     *
     * @param job a JasonObjectBuilder into which field names (keys) and field
     * values are to be inserted.
     * @throws IOException if problem obtaining / parsing data
     */
    abstract protected void _diffs(JsonObjectBuilder job) throws IOException;

    /**
     * Delete this Entity.
     *
     * @throws IOException if problem obtaining / parsing data
     */
    public final void remove() throws IOException {
        EntityState oldState = getState();
        switch (oldState) {
            case NEW:
            case NEWEDITING:
                _remove();
                em.removeFromTransientCache((E) this);
                setState(REMOVED);
                fireStateChange(REMOVE, oldState, REMOVED);
                return;
            case DBENTITY:
            case DBENTITYEDITING:
                _remove();
                entityPersistenceManager.delete(getId());
                em.removeFromCache((E) this);
                setState(REMOVED);
                fireStateChange(REMOVE, oldState, REMOVED);
                return;
            default:
                throw new LogicException("Should not be trying to remove an entity in " + oldState + " state");
        }
    }

    /**
     * Complete any entity specific removal actions prior to entity deletion.
     * Basic use case: remove any linkage to parent entities.
     *
     * @throws java.io.IOException
     */
    abstract protected void _remove() throws IOException;

    /**
     * Copy entity fields into this entity.
     *
     * @param e the copy source entity
     * @throws java.io.IOException
     */
    public final void copy(E e) throws IOException {
        EntityState oldState = getState();
        if (oldState == NEW || oldState == NEWEDITING) {
            _copy(e);
            dbfields.copy(e);
            ensureEditing();
            fireFieldChange(ALL);
            return;
        }
        throw new IOException("Should not be trying to copy an entity in " + oldState + " state");
    }

    /**
     * Field Copy actions - copy entity fields into this entity.
     *
     * @param from the copy source entity
     */
    abstract protected void _copy(E from);

    private class EntityStateChangeListener extends Listener<EntityStateChangeEventParams> {

        public EntityStateChangeListener(String name) {
            super(name);
        }

        @Override
        public void action(EntityStateChangeEventParams p) {
            switch (p.getTransition()) {
                case EDIT:
                    savable.add();
                    break;
                case LOAD:
                    savable.remove();
                    break;
                case SAVE:
                    break;
                case REMOVE:
                    savable.remove();
                    break;
                case RESET:
                    savable.remove();
            }
        }
    }

    @Override
    public Image getIcon() {
        return checkRules() ? super.getIcon() : getIconWithError();
    }

    private class EntitySavable<E, P, F> extends AbstractSavable implements Icon, HasInstanceDescription {

        private Icon icon;

        public void add() {
            if (getLookup().lookup(EntitySavable.class) == null) {
                register();
                addLookupContent(this);
            }
            icon = null;
        }

        public void remove() {
            removeLookupContent(this);
            unregister();
        }

        @Override
        public String instanceDescription() {
            return LogBuilder.instanceDescription(this, entityname);
        }

        @Override
        protected void handleSave() throws IOException {
            LogBuilder.writeLog("nbpcglibrary.data", this, "handleSave");
            if (EntityRW.this.save()) {
                removeLookupContent(this);
            } else {
                EventQueue.invokeLater(new ReRegister());
                LibraryOnStop.incRegisterOutstanding();
            }
        }

        private class ReRegister implements Runnable {

            @Override
            public void run() {
                EntitySavable.this.register();
                LibraryOnStop.decRegisterOutstanding();
            }
        }

        @Override
        protected String findDisplayName() {
            return getDisplayTitle();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof EntitySavable) {
                return entity() == ((EntitySavable) obj).entity();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return entity().hashCode();
        }

        EntityRW entity() {
            return EntityRW.this;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            new ImageIcon(getIcon()).paintIcon(c, g, x, y);
        }

        @Override
        public int getIconWidth() {
            if (icon == null) {
                icon = new ImageIcon(getIcon());
            }
            return icon.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            if (icon == null) {
                icon = new ImageIcon(getIcon());
            }
            return icon.getIconHeight();
        }
    }
}
