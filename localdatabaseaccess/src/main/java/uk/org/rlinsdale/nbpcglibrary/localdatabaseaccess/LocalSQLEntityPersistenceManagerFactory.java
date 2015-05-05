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
package uk.org.rlinsdale.nbpcglibrary.localdatabaseaccess;

import java.util.Properties;
import org.openide.util.lookup.ServiceProvider;
import uk.org.rlinsdale.nbpcglibrary.api.EntityPersistenceManager;
import uk.org.rlinsdale.nbpcglibrary.api.EntityPersistenceManagerFactory;

/**
 * A Factory to create DataServices for local SQL datasources.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
@ServiceProvider(service = EntityPersistenceManagerFactory.class)
public class LocalSQLEntityPersistenceManagerFactory implements EntityPersistenceManagerFactory<LocalSQLDataAccessManager, LocalSQLDataAccessManagerFactory> {

    @Override
    public String getType() {
        return "localsql";
    }
    
    @Override
    public Class<LocalSQLDataAccessManagerFactory> getDataAccessFactoryClass() {
        return LocalSQLDataAccessManagerFactory.class;
    }

    @Override
    public EntityPersistenceManager createEntityPersistenceManager(String entityname, Properties p, LocalSQLDataAccessManager dam) {
            return new LocalSQLEntityPersistenceManager(entityname, p, dam);
    }
}
