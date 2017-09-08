/*
 * Copyright 2017 richard linsdale.
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
package uk.theretiredprogrammer.nbpcglibrary.api;

import java.util.List;
import java.util.Map;

/**
 * The Rest interface to interface with Rest services, including permanent
 * storage solutions - databases, html rest services etc.
 *
 * @author richard linsdale (richard at theretiredprogrammer.uk)
 * @param <E> the entity class being transferred
 */
public interface Rest<E> {

    /**
     * Get an entity
     *
     * @param location the uri for the entity
     * @return the entity or null if a problem
     */
    public E get(String location);

    /**
     * Get a list of Entities
     *
     * @param location the uri for the entitylist
     * @return the entity list or null if a problem
     */
    public List<E> getAll(String location);

    /**
     * Create a new entity
     *
     * @param location the uri for creating new entities
     * @param entity the entity being saved
     * @return the created entity as now stored in the permanent store or null
     * if a problem
     */
    public E create(String location, E entity);

    /**
     * Update an entity, replacing it entirely with the provided entity. Will do
     * a create if the request URI does not exist.
     *
     * @param location the uri for the entity to be replaced
     * @param entity the entity to be used for the update
     * @return the updated/created entity as now stored in the permanent store
     * or null if a problem
     */
    public E update(String location, E entity);

    /**
     * Patch an existing entity.
     *
     * @param location the URi of the entity to be patched
     * @param patches a map, containing field names and values which are to be
     * updated
     * @return the updated entity as now stored in the permanent store or null
     * if a problem
     */
    public E patch(String location, Map<String, Object> patches);

    /**
     * Delete an existing entity
     *
     * @param location the uri of the entity to be deleted
     * @return true if deleted, false if a problem
     */
    public boolean delete(String location);

}
