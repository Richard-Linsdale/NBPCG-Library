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
package uk.org.rlinsdale.nbpcglibrary.topcomponent;

import java.util.ArrayList;
import java.util.List;
import uk.org.rlinsdale.nbpcglibrary.common.Listener;
import uk.org.rlinsdale.nbpcglibrary.data.entity.EntityRO;
import uk.org.rlinsdale.nbpcglibrary.data.entity.EntityFieldChangeEventParams;
import uk.org.rlinsdale.nbpcglibrary.data.entity.SetChangeEventParams;
import uk.org.rlinsdale.nbpcglibrary.form.ChoiceField;
import uk.org.rlinsdale.nbpcglibrary.form.ChoiceFieldBackingObject;

/**
 * Choice Field - taking values from all entities of a class.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 * @param <E> the entity class
 * @param <F> the entity fields enum class
 */
public abstract class ReferenceChoiceCollectionFields<E extends EntityRO, F> extends ChoiceField {

    private List<E> choices;
    private List<String> choiceText;
    private final ChoicesFieldListener choicesfieldListener;
    private final CollectionFieldListener collectionfieldListener;

    /**
     * Constructor.
     *
     * @param backingObject the backingObject
     * @param label the field label
     */
    public ReferenceChoiceCollectionFields(ChoiceFieldBackingObject backingObject, String label) {
        super(backingObject, label);
        choicesfieldListener = new ChoicesFieldListener(label + "/choices");
        collectionfieldListener = new CollectionFieldListener(label + "/setchange");
        postFieldUpdateAction();
    }

    /**
     * finish managing the choices text
     */
    public void closeChoices() {
        removeCollectionListeners(collectionfieldListener);
        removeChoicesListeners();
    }

    private void addChoicesListeners() {
        choices.stream().filter((e) -> (e != null)).forEach((e) -> {
            e.addFieldListener(choicesfieldListener);
        });
    }

    private void removeChoicesListeners() {
        choices.stream().filter((e) -> (e != null)).forEach((e) -> {
            e.removeFieldListener(choicesfieldListener);
        });
    }

    @Override
    protected  List<String> getChoicesText() {
        choiceText = new ArrayList<>();
        choices = getChoicesEntities();
        choices.stream().forEach((e) -> {
            choiceText.add(convertEntitytoText(e));
        });
        return choiceText;
    }

    /**
     * Get the set of entities.
     *
     * @return the set of entities
     */
    protected abstract List<E> getChoicesEntities();

    /**
     * Get the Choice text from an entity.
     *
     * @param e the entity
     * @return the choice text
     */
    protected abstract String convertEntitytoText(E e);

    /**
     * Get the Choice entity from its text form.
     *
     * @param text the textual form
     * @return the choice entity
     */
    public E convertTexttoEntity(String text) {
        for (int i = 0; i < choiceText.size(); i++) {
            if (choiceText.get(i).equals(text)) {
                return choices.get(i);
            }
        }
        return null;
    }

    /**
     * add a given listener to all parent collections which could affect this
     * reference choice.
     *
     * @param listener the set change listener
     */
    protected abstract void addCollectionListeners(Listener<SetChangeEventParams> listener);

    /**
     * remove a given listener from all parent collections which could affect
     * this reference choice.
     *
     * @param listener the set change listener
     */
    protected abstract void removeCollectionListeners(Listener<SetChangeEventParams> listener);

    /**
     * hook to allow actions to take place before updating a combobox
     */
    @Override
    public final void preFieldUpdateAction() {
        removeCollectionListeners(collectionfieldListener);
        removeChoicesListeners();
    }
    
    /**
     * hook to allow actions to take place after updating a combobox
     */
    @Override
    public final void postFieldUpdateAction() {
        addChoicesListeners();
        addCollectionListeners(collectionfieldListener);
    }
    
    private class CollectionFieldListener extends Listener<SetChangeEventParams> {

        public CollectionFieldListener(String name) {
            super(name);
        }

        @Override
        public void action(SetChangeEventParams p) {
            updateChoicesFromBackingObject();
        }
    }

    private class ChoicesFieldListener extends Listener<EntityFieldChangeEventParams<F>> {

        public ChoicesFieldListener(String name) {
            super(name);
        }

        @Override
        public void action(EntityFieldChangeEventParams<F> p) {
            updateChoicesFromBackingObject();
        }
    }
}