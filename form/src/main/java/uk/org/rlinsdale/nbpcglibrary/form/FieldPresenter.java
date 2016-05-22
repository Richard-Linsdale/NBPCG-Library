/*
 * Copyright (C) 2014-2016 Richard Linsdale (richard.linsdale at blueyonder.co.uk).
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
import uk.org.rlinsdale.nbpcglibrary.api.BadFormatException;

/**
 * Presenter for a field on a form
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 * @param <T> type of the data contained in the field
 */
public class FieldPresenter<T> implements Presenter<FieldViewAPI<T>>, ActionListener, FocusListener {

    private FieldViewAPI<T> view;
    private FieldModel<T> model;
    private T lastvaluesetinfield;
    private boolean inhibitListeneractions = false;
    private final String id;

    /**
     * Constructor
     *
     * @param view the view to be used with this presenter
     * @param model the model to be used with this presenter
     */
    public FieldPresenter(FieldViewAPI<T> view, FieldModel<T> model) {
        this("", view, model);
    }

    /**
     * Constructor
     *
     * @param id the field ID (for use in error messages.
     * @param view view the view to be used with this presenter
     * @param model the model to be used with this presenter
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public FieldPresenter(String id, FieldViewAPI<T> view, FieldModel<T> model) {
        this.id = id;
        this.model = model;
        this.view = view;
        view.addFocusListener(this);
        view.addActionListener(this);
    }

    @Override
    public FieldViewAPI<T> getView() {
        return view;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        fieldActionHandler();
    }

    @Override
    public void focusGained(FocusEvent fe) {
    }

    @Override
    public void focusLost(FocusEvent fe) {
        fieldActionHandler();
    }

    private void fieldActionHandler() {
        if (!inhibitListeneractions) {
            StringBuilder sb = new StringBuilder();
            view.setErrorMarker(updateModelFromView(sb) ? null : sb.toString());
        }
    }

    private boolean updateModelFromView(StringBuilder sb) {
        T value;
        try {
            value = view.get();
        } catch (BadFormatException ex) {
            sb.append("Badly Formatted");
            return false;
        }
        if (value == null) {
            if (lastvaluesetinfield != null) {
                model.set(value);
                updateViewFromModel();
            }
        } else {
            if (!value.equals(lastvaluesetinfield)) {
                model.set(value);
                updateViewFromModel();
            }
        }
        return model.test(sb);
    }

    /**
     * Set an updated value in the model and present it in the view
     *
     * @param value the new value to insert
     */
    public void set(T value) {
        model.set(value);
        updateViewFromModel();
        StringBuilder sb = new StringBuilder();
        view.setErrorMarker(model.test(sb) ? null : sb.toString());
    }

    private void updateViewFromModel() {
        inhibitListeneractions = true;
        view.setNullSelectionAllowed(model.isNullSelectionAllowed());
        view.setChoices(model.getChoices());
        lastvaluesetinfield = model.get();
        view.set(lastvaluesetinfield);
        inhibitListeneractions = false;
    }

    @Override
    public boolean test(StringBuilder sb) {
        StringBuilder lsb = new StringBuilder();
        if (updateModelFromView(lsb)) {
            view.setErrorMarker(null);
            return true;
        }
        String lsbs = lsb.toString();
        view.setErrorMarker(lsbs);
        if (!id.equals("")) {
            sb.append(id);
            sb.append(": ");
        }
        sb.append(lsbs);
        sb.append("; ");
        return false;
    }

    @Override
    public void enableView() {
        updateViewFromModel();
        view.setErrorMarker(null);
    }

    @Override
    public void refreshView() {
        StringBuilder sb = new StringBuilder();
        updateViewFromModel();
        view.setErrorMarker(model.test(sb) ? null : sb.toString());
    }
}
