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

import uk.org.rlinsdale.nbpcglibrary.common.Rules;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTextArea;
import uk.org.rlinsdale.nbpcglibrary.annotations.RegisterLog;
import uk.org.rlinsdale.nbpcglibrary.common.Event;
import uk.org.rlinsdale.nbpcglibrary.common.LogBuilder;
import uk.org.rlinsdale.nbpcglibrary.common.HasInstanceDescription;
import uk.org.rlinsdale.nbpcglibrary.common.Listener;
import uk.org.rlinsdale.nbpcglibrary.common.SimpleEventParams;
import static uk.org.rlinsdale.nbpcglibrary.form.Form.FormSaveResult.SAVEFAIL;
import static uk.org.rlinsdale.nbpcglibrary.form.Form.FormSaveResult.SAVESUCCESS;
import static uk.org.rlinsdale.nbpcglibrary.form.Form.FormSaveResult.SAVEVALIDATIONFAIL;

/**
 * A Form object which can be displayed in a dialog box.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
@RegisterLog("nbpcglibrary.form")
public class Form extends GridBagPanel implements HasInstanceDescription {

    public enum FormSaveResult {

        SAVESUCCESS,
        SAVEVALIDATIONFAIL,
        SAVEFAIL,
        CANCELLED,
        CLOSED
    }

    private JTextArea failuremessages;
    private String formname;
    private final List<FieldsDef> fieldsdefs;
    private Rules additionalRules;
    private final Event<SimpleEventParams> cancelEvent;

    /**
     * Constructor
     *
     * @param formname the form's name
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public Form(String formname) {
        fieldsdefs = new ArrayList<>();
        this.formname = formname;
        cancelEvent = new Event<>(instanceDescription() + "-cancel");
        LogBuilder.writeConstructorLog("nbpcglibrary.form", this, formname);
    }

    /**
     * Constructor
     *
     * @param formname the form's name
     * @param fieldsdef the collection of fields to be displayed in this form
     */
    public Form(String formname, FieldsDef fieldsdef) {
        this(formname);
        addFieldsdef(fieldsdef);
        finaliseForm();
    }

    @Override
    public final String instanceDescription() {
        return LogBuilder.instanceDescription(this, formname);
    }

    /**
     * Add a collection of fields for display on this form
     *
     * @param fieldsdef the collection of fields
     */
    public final void addFieldsdef(FieldsDef fieldsdef) {
        if (fieldsdef != null) {
            LogBuilder.writeLog("nbpcglibrary.form", this, "addFieldsdef", fieldsdef);
            fieldsdefs.add(fieldsdef);
            fieldsdef.getFields().stream().forEach((field) -> {
                addRow(field.getComponents());
            });
        }
    }

    /**
     * Set additional form level rules.
     *
     * @param additionalRules the additional rule set for this form
     */
    public void setAdditionalRules(Rules additionalRules) {
        this.additionalRules = additionalRules;
    }

    /**
     * Add cancel Listener.
     *
     * @param listener the listener which is fired on cancel
     */
    public void addCancelListener(Listener<SimpleEventParams> listener) {
        cancelEvent.addListener(listener);
    }

    /**
     * Finalise the construction of the form.
     */
    public final void finaliseForm() {
        finaliseForm(50);
    }

    /**
     * Finalise the construction of the form.
     *
     * @param msgwidth the width of the failure message areas (used to display
     * any failure messages due to rule set failures)
     */
    public final void finaliseForm(int msgwidth) {
        LogBuilder.writeLog("nbpcglibrary.form", this, "finaliseForm", msgwidth);
        failuremessages = new JTextArea(3, msgwidth);
        failuremessages.setForeground(Color.red);
        failuremessages.setEditable(false);
        failuremessages.setFocusable(false);
        addSpannedRow(failuremessages, Color.LIGHT_GRAY);
    }

    /**
     * do the form save actions: save the field values check the form rules are
     * ok, and save to backingObject if OK if ok do the fielddefs save action
     *
     * @return save result code
     */
    public FormSaveResult save() {
        boolean ok = true;
        failuremessages.setText("");
        LogBuilder.writeLog("nbpcglibrary.form", this, "testsave");
        for (FieldsDef f : fieldsdefs) {
            if (!f.testAndSaveAllFields()) {
                ok = false;
            }
        }
        if (!ok) {
            writeAllFailureMessages();
            return SAVEVALIDATIONFAIL;
        }
        ok = true;
        LogBuilder.writeLog("nbpcglibrary.form", this, "save");
        for (FieldsDef f : fieldsdefs) {
            if (!f.save()) {
                ok = false;
            }
        }
        return ok ? SAVESUCCESS : SAVEFAIL;
    }
    
    List<String> getParameters() {
        List<String> parameters = new ArrayList<>();
        fieldsdefs.stream().forEach((f) -> {
            String[] params = f.getParameters();
            if (params!= null) {
                parameters.addAll(Arrays.asList(params));
            }
        });
        return parameters;
    }

    /**
     * Cancel action
     */
    public void cancel() {
        LogBuilder.writeLog("nbpcglibrary.form", this, "cancel");
        cancelEvent.fire(new SimpleEventParams());
    }

    /**
     * Set the values of fields in the form.
     */
    public void updateAllFieldsFromBackingObject() {
        LogBuilder.writeLog("nbpcglibrary.form", this, "updateAllFieldsFromBackingObject");
        failuremessages.setText("");
        fieldsdefs.stream().forEach((f) -> {
            f.updateAllFieldsFromBackingObject();
        });
    }

    /**
     * Check if all rules in the form (at form, fieldsdef and field levels) are
     * valid.
     *
     * @return true if all rules are valid
     */
    public boolean checkRules() {
        boolean valid = true;
        for (FieldsDef f : fieldsdefs) {
            if (!f.checkRules()) {
                valid = false;
            }
        }
        if (additionalRules != null) {
            if (!additionalRules.checkRules()) {
                valid = false;
            }
        }
        LogBuilder.writeExitingLog("nbpcglibrary.form", this, "checkRules", valid);
        return valid;
    }

    /**
     * Collect all failures messages from any failing rule (at form, fieldsdef
     * and field levels), and display the resulting message in the failure
     * message area of the form.
     */
    public final void writeAllFailureMessages() {
        StringBuilder sb = new StringBuilder();
        addFailureMessages(sb);
        if (additionalRules != null) {
            additionalRules.addFailureMessages(sb);
        }
        String t = sb.toString();
        LogBuilder.writeExitingLog("nbpcglibrary.form", this, "writeAllFailureMessages", t.replace("\n", "; "));
        failuremessages.setText(t);
    }

    private void addFailureMessages(StringBuilder msg) {
        fieldsdefs.stream().forEach((f) -> {
            f.addFailureMessages(msg);
        });
    }
}
