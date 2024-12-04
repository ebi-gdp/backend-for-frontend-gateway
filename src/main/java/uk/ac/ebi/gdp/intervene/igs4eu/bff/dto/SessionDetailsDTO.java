/*
 *
 * Copyright 2024 EMBL - European Bioinformatics Institute
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
 *
 */
package uk.ac.ebi.gdp.intervene.igs4eu.bff.dto;

public class SessionDetailsDTO {
    private final String id;
    private final String lastAccessTime;
    private final String creationTime;
    private final String maxIdleTime;
    private final boolean expired;

    public SessionDetailsDTO(final String id,
                             final String lastAccessTime,
                             final String creationTime,
                             final String maxIdleTime,
                             final boolean expired) {
        this.id = id;
        this.lastAccessTime = lastAccessTime;
        this.creationTime = creationTime;
        this.maxIdleTime = maxIdleTime;
        this.expired = expired;
    }

    public String getId() {
        return id;
    }

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public String getMaxIdleTime() {
        return maxIdleTime;
    }

    public boolean isExpired() {
        return expired;
    }
}
