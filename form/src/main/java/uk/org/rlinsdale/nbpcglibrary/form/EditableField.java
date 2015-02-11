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
package uk.org.rlinsdale.nbpcglibrary.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JComponent;

/**
 * Abstract Class representing an editable Field on a Form
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 * @param <T> type of the data connecting to the backing Object
 */
public abstract class EditableField<T> extends Field<T> {

    private final FieldActionListener actionListener = new FieldActionListener();
    private final FieldFocusListener focusListener = new FieldFocusListener();
    private final JComponent field;
    protected T lastvaluesetinfield;
    private final FieldBackingObject<T> backingObject;

    /**
     * Constructor
     *
     * @param backingObject the backing object
     * @param label the label text for this field
     * @param field the swing field component
     * @param additionalfield optional additional field (set to null if not required)
     */
    public EditableField(FieldBackingObject<T> backingObject, String label, JComponent field, JComponent additionalfield) {
        super(backingObject, label, field, additionalfield);
        this.backingObject = backingObject;
        this.field = field;
    }
    
    
    /**
     * setField an action listener for this field
     *
     * @param listener the listener
     */
    abstract void addActionListener(ActionListener listener);

    /**
     * remove an action listener for this field
     *
     * @param listener the listener
     */
    abstract void removeActionListener(ActionListener listener);

    @Override
    final void setField(T value) {
        removeActionListener(actionListener);
        field.removeFocusListener(focusListener);
        this.lastvaluesetinfield = value;
        set(value);
        checkRules();
        addActionListener(actionListener);
        field.addFocusListener(focusListener);
    }

    /**
     * Set a value into the Field
     *
     * @param value the value to be inserted into the Field
     */
    abstract void set(T value);

    
    @Override
    public void updateFieldFromBackingObject() {
        T value = backingObject.get();
        if (!value.equals(lastvaluesetinfield)) {
            lastvaluesetinfield = value;
            setField(value);
        }
    }
    
    @Override
    public void updateBackingObjectFromField() {
        backingObject.set(get());
    }

    /**
     * Get a value from the field
     *
     * @return the value of the field
     */
    abstract T get();

    private class FieldActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            updateIfChange(get());
        }
    }

    private class FieldFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent fe) {
        }

        @Override
        public void focusLost(FocusEvent fe) {
            updateIfChange(get());
        }
    }

    /**
     * check that the value has changed and if so then update the lastvalue
     * variable, and update the backingobject if rule check is passed
     *
     * @param value the new value to test
     */
    abstract void updateIfChange(T value);

}
