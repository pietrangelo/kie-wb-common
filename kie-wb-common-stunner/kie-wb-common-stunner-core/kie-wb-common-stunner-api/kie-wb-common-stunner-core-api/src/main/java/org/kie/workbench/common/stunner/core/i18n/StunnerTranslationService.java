/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.i18n;

/**
 * Handles the i18n on stunner
 */
public interface StunnerTranslationService {

    /**
     * Retrieves the description for the given Definition Set ID
     */
    String getDefinitionSetDescription(String defSetId);

    /**
     * Retrieves the name for the given Property Set ID
     */
    String getPropertySetName(String proepSetId);

    /**
     * Retrieves the title for the given Definition ID
     */
    String getDefinitionTitle(String defId);

    /**
     * Retrieves the category for the given Definition ID
     */
    String getDefinitionCategory(String defId);

    /**
     * Retrieves the description for the given Definition ID
     */
    String getDefinitionDescription(String defId);

    /**
     * Retrieves the caption for the given Property ID
     */
    String getPropertyCaption(String propId);

    /**
     * Retrieves the description for the given Property ID
     */
    String getPropertyDescription(String propId);
}
