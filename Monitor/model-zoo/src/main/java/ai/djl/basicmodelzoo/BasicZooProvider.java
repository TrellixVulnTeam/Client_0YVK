/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package ai.djl.basicmodelzoo;

import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooProvider;

/**
 * An imperative model zoo provider implements the {@link ZooProvider}
 * interface.
 */
public class BasicZooProvider implements ZooProvider {

    /** {@inheritDoc} */
    @Override
    public ModelZoo getModelZoo() {
        return new BasicModelZoo();
    }
}
