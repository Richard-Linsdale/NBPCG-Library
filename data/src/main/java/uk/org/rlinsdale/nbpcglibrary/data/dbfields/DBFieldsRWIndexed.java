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
package uk.org.rlinsdale.nbpcglibrary.data.dbfields;

import uk.org.rlinsdale.nbpcglibrary.data.entity.Entity;

/**
 * Interface for handling entity field states for an entity which uses
 * a index field (integer) for ordering entities.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 * @param <E> the entity class
 */
public interface DBFieldsRWIndexed<E extends Entity> extends DBFields<E> {

    /**
     * Get the index value.
     *
     * @return the index value
     */
    public int getIndex();

    /**
     * Set the index value.
     *
     * @param idx the index value
     */
    public void setIndex(int idx);
}
