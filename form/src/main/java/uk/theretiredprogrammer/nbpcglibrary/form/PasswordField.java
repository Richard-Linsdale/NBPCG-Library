/*
 * Copyright 2015-2017 Richard Linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.nbpcglibrary.form;

import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JPasswordField;

/**
 * A Field to handle password entry.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PasswordField extends FieldView<String> {

    private final JPasswordField fieldcomponent;

    /**
     * Constructor
     */
    public PasswordField() {
        this(new JPasswordField(), 20);
    }
    
    /**
     * Constructor
     *
     * @param size the size of the text field object
     */
    public PasswordField(int size) {
        this(new JPasswordField(), size);
    }

    private PasswordField(JPasswordField fieldcomponent, int size) {
        super(fieldcomponent);
        this.fieldcomponent = fieldcomponent;
        fieldcomponent.setColumns(size);
    }

    @Override
    public final String get() {
        return new String(fieldcomponent.getPassword());
    }

    @Override
    public final void set(String value) {
        fieldcomponent.setText(value);
    }
    
    @Override
    public void addActionListener(ActionListener listener) {
        fieldcomponent.addActionListener(listener);
    }
}
