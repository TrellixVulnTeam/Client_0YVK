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
package main.java.ai.djl.nn.recurrent;

import ai.djl.nn.Block;
import ai.djl.nn.recurrent.RecurrentBlock;
import ai.djl.util.Preconditions;

/**
 * {@code GRU} is an abstract implementation of recurrent neural networks which applies GRU (Gated
 * Recurrent Unit) recurrent layer to input.
 *
 * <p>Current implementation refers the [paper](http://arxiv.org/abs/1406.1078) - Gated Recurrent
 * Unit. The definition of GRU here is slightly different from the paper but compatible with CUDNN.
 *
 * <p>The GRU operator is formulated as below:
 *
 * <p>$$ \begin{split}\begin{array}{ll} r_t = \mathrm{sigmoid}(W_{ir} x_t + b_{ir} + W_{hr}
 * h_{(t-1)} + b_{hr}) \\ z_t = \mathrm{sigmoid}(W_{iz} x_t + b_{iz} + W_{hz} h_{(t-1)} + b_{hz}) \\
 * n_t = \tanh(W_{in} x_t + b_{in} + r_t * (W_{hn} h_{(t-1)}+ b_{hn})) \\ h_t = (1 - z_t) * n_t +
 * z_t * h_{(t-1)} \\ \end{array}\end{split} $$
 */
public class GRU extends RecurrentBlock {

    GRU(Builder builder) {
        super(builder);
        mode = "gru";
        gates = 3;
    }

    /**
     * Creates a builder to build a {@link GRU}.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /** The Builder to construct a {@link GRU} type of {@link Block}. */
    public static final class Builder extends BaseBuilder<Builder> {

        /** {@inheritDoc} */
        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds a {@link GRU} block.
         *
         * @return the {@link GRU} block
         */
        public GRU build() {
            Preconditions.checkArgument(
                    stateSize > 0 && numStackedLayers > 0,
                    "Must set stateSize and numStackedLayers");
            return new GRU(this);
        }
    }
}
