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

import java.util.HashMap;
import java.util.Map;
import uk.org.rlinsdale.nbpcglibrary.common.Event;
import uk.org.rlinsdale.nbpcglibrary.common.Listener;
import uk.org.rlinsdale.nbpcglibrary.common.LogicException;
import uk.org.rlinsdale.nbpcglibrary.data.dataaccess.DataAccessRW;
import uk.org.rlinsdale.nbpcglibrary.data.dbfields.DBFieldsRW;
import uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState.DBENTITY;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState.NEW;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityState.REMOVED;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityStateChange.REMOVE;
import static uk.org.rlinsdale.nbpcglibrary.data.entity.EntityStateChangeEventParams.EntityStateChange.SAVE;
import uk.org.rlinsdale.nbpcglibrary.data.entityreferences.IdChangeEventParams;

/**
 * The abstract class defining an editable Entity.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 * @param <E> the entity class
 * @param <P> the Parent Entity Class
 */
public abstract class EntityRW<E extends EntityRW, P extends Entity> extends EntityRO {

    private final DBFieldsRW<E> dbfields;
    private final DataAccessRW dataAccess;
    private final Event<IdChangeEventParams> idChangeEvent;
    private final EntityManagerRW<E, P> em;

    /**
     * Constructor.
     *
     * @param entityname the entity name
     * @param id the entity Id
     * @param em the entity manager for this entity class
     * @param dbfields the entity fields
     */
    public EntityRW(String entityname, int id, EntityManagerRW<E, P> em, DBFieldsRW<E> dbfields) {
        this(entityname, id, em, em.getDataAccess(), dbfields);
    }

    private EntityRW(String entityname, int id, EntityManagerRW<E, P> em, DataAccessRW dataAccess, DBFieldsRW<E> dbfields) {
        super(entityname, id, dataAccess, dbfields);
        this.em = em;
        this.dataAccess = dataAccess;
        this.dbfields = dbfields;
        idChangeEvent = new Event<>(entityname + "/id");
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
        fireFieldChange(FieldChangeEventParams.IDFIELD);
    }

    /**
     * Save this entity to entity storage.
     *
     * @return true
     */
    public boolean save() {
        Map<String, Object> map;
        EntityState oldState = getState();
        switch (oldState) {
            case DBENTITY:
                return true; //we don't need to do anything as this is a straight copy of a db entity
            case INIT:
                throw new LogicException("Should not be trying to save an entity in INIT state");
            case REMOVED:
                throw new LogicException("Should not be trying to save an entity in REMOVED state");
            case NEW:
            case NEWEDITING:
                if (!checkRules()) {
                    throw new LogicException("save() on an entity which is not valid");
                }
                map = new HashMap<>();
                _values(map);
                dbfields.values(map);
                em.persistTransient((E) this, dataAccess.insert(map));
                idChangeEvent.fire(new IdChangeEventParams());
                setState(DBENTITY);
                break;
            case DBENTITYEDITING:
                if (!checkRules()) {
                    throw new LogicException("save() on an entity which is not valid");
                }
                map = new HashMap<>();
                _diffs(map);
                dbfields.diffs(map);
                if (!map.isEmpty()) {
                    dataAccess.update(getId(), map);
                }
                setState(DBENTITY);
                break;
            default:
                if (!checkRules()) {
                    throw new LogicException("save() on an entity which is not valid");
                }
        }
        fireStateChange(SAVE, oldState, DBENTITY);
        return true;
    }

    /**
     * Add all field values to the given map.
     *
     * @param map the map into which field names (keys) and field values are to
     * be inserted.
     */
    abstract protected void _values(Map<String, Object> map);

    /**
     * Add any modified field values to the given map.
     *
     * @param map the map into which field names (keys) and field values are to
     * be inserted.
     */
    abstract protected void _diffs(Map<String, Object> map);

    /**
     * Delete this Entity.
     */
    public final void remove() {
        EntityState oldState = getState();
        switch (oldState) {
            case NEW:
            case NEWEDITING:
                _remove();
                em.removeFromTransientCache((E) this);
                setState(REMOVED);
                fireStateChange(REMOVE, oldState, REMOVED);
                removeEntityRegistration();
                return;
            case DBENTITY:
            case DBENTITYEDITING:
                _remove();
                dataAccess.delete(getId());
                em.removeFromCache((E) this);
                setState(REMOVED);
                fireStateChange(REMOVE, oldState, REMOVED);
                removeEntityRegistration();
                return;
            default:
                throw new LogicException("Should not be trying to remove an entity in " + oldState + " state");
        }
    }

    /**
     * Complete any entity specific removal actions prior to entity deletion.
     * Basic use case: remove any linkage to parent entities.
     */
    abstract protected void _remove();

    /**
     * Copy entity fields into this entity.
     *
     * @param e the copy source entity
     */
    public final void copy(E e) {
        EntityState oldState = getState();
        if (oldState == NEW) {
            _copy(e);
            dbfields.copy(e);
            ensureEditing();
            fireFieldChange(FieldChangeEventParams.ALLFIELDS);
            return;
        }
        throw new LogicException("Should not be trying to copy an entity in " + oldState + " state");
    }

    /**
     * Field Copy actions - copy entity fields into this entity.
     *
     * @param from the copy source entity
     */
    abstract protected void _copy(E from);
}
