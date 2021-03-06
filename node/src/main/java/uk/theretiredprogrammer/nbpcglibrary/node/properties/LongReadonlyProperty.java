/*
 * Copyright 2014-2017 Richard Linsdale.
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
package uk.theretiredprogrammer.nbpcglibrary.node.properties;

import org.openide.nodes.PropertySupport;

/**
 * Long Readonly Property item
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class LongReadonlyProperty extends PropertySupport.ReadOnly<String> {

    private String val;

    /**
     * Constructor
     *
     * @param name the property name
     * @param val the property value
     */
    public LongReadonlyProperty(String name, Long val) {
        super(name, String.class, name, name);
        setName(name);
        update(val);
    }
    
    /**
     * Constructor
     *
     * @param name the property name
     * @param val the property value
     */
    public LongReadonlyProperty(String name, long val) {
        super(name, String.class, name, name);
        setName(name);
        update(val);
    }
    
    /**
     * Update the value
     *
     * @param val the value used to update
     */
    public final void update(long val) {
        this.val = Long.toString(val);
    }

    @Override
    public String getValue() {
        return val;
    }
}
