/*
 * Copyright 2017 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.nbpcglibrary.filerest;

import uk.theretiredprogrammer.nbpcglibrary.api.IdTimestampBaseEntity;

/**
 * The Claim BaseEntity.
 *
 * (Class generated by NetBeans Platform Code Generator tools using script.xml.
 * Do not edit this file. Apply any changes to the definition file and
 * regenerate all files.)
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ClaimEntity extends IdTimestampBaseEntity {
    
    private String claimkey;
    private String value;
    private Integer user;

    /**
     * Constructor - ClaimEntity.
     */
    public ClaimEntity() {
        super();
    }

    /**
     * Constructor - ClaimEntity.
     * @param from the entity to copy from
     */
    public ClaimEntity(ClaimEntity from) {
        super.copy(false, from);
        claimkey = from.claimkey;
        value = from.value;
        user = from.user;
    }

    /**
     * Get the claimkey.
     *
     * @return the claimkey
     */
    public final String getClaimkey() {
        return claimkey;
    }

    /**
     * Define the Claimkey.
     *
     * @param claimkey the claimkey
     */
    public void setClaimkey(String claimkey) {
        this.claimkey = claimkey;
    }

    /**
     * Get the value.
     *
     * @return the value
     */
    public final String getValue() {
        return value;
    }

    /**
     * Define the Value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the user.
     *
     * @return the user
     */
    public final Integer getUser() {
        return user;
    }

    /**
     * Define the User.
     *
     * @param user the user
     */
    public void setUser(Integer user) {
        this.user = user;
    }

    @Override
    public boolean isMatch(String fieldname, int fieldvalue) {
        return (fieldname.equals("user")) && (fieldvalue == user);
    }
}
