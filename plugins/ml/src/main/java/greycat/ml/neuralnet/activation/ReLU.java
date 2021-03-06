/**
 * Copyright 2017-2019 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.ml.neuralnet.activation;

public class ReLU implements Activation {

    private static ReLU static_unit = null;

    public static ReLU instance() {
        if (static_unit == null) {
            static_unit = new ReLU();
        }
        return static_unit;
    }

    @Override
    public double forward(double x) {
        if (x >= 0) {
            return x;
        } else {
            return 0;
        }
    }

    @Override
    public double backward(double x, double fct) {
        if (x >= 0) {
            return 1.0;
        } else {
            return 0;
        }
    }

    @Override
    public int getId() {
        return Activations.RELU;
    }

    @Override
    public double getParam() {
        return 0;
    }
}
