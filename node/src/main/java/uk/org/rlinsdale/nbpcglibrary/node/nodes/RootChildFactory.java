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
package uk.org.rlinsdale.nbpcglibrary.node.nodes;

import uk.org.rlinsdale.nbpcglibrary.data.entity.Entity;
import uk.org.rlinsdale.nbpcglibrary.data.entity.SetChangeEventParams;
import uk.org.rlinsdale.nbpcglibrary.common.Listener;
import org.openide.nodes.ChildFactory;

/**
 * Root ChildFactory support
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 * @param <E> the Parent Entity Class
 */
public abstract class RootChildFactory<E extends Entity> extends ChildFactory<Entity> {

    private final E parentEntity;

    /**
     * Constructor
     *
     * @param parentEntity the parent entity
     */
    public RootChildFactory(E parentEntity) {
        this.parentEntity = parentEntity;
    }

    /**
     * Get the parent entity.
     *
     * @return the parent entity
     */
    public E getParentEntity() {
        return parentEntity;
    }

    /**
     * get Change listener for the child entity set.
     *
     * @param name the name of the listener (for reporting/logging)
     * @return  the required listener
     */
    public Listener<SetChangeEventParams> getSetChangeListener(String name) {
        return new ChildSetChangeListener(name);
    }

    private class ChildSetChangeListener extends Listener<SetChangeEventParams> {

        public ChildSetChangeListener(String name) {
            super(name);
        }

        @Override
        public void action(SetChangeEventParams p) {
            refresh(true);
        }
    }
}
