/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package test.java.ai.djl.nn.convolutional;

import ai.djl.ndarray.types.Shape;
import ai.djl.nn.convolutional.OutputShapeTest;
import ai.djl.nn.convolutional.ShapeUtils;
import ai.djl.util.Pair;
import java.util.stream.IntStream;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Conv3dTest extends OutputShapeTest {

    @Test
    public void testOutputShapes() {
        Range filters = Range.ofClosed(1, 2);

        Range depths = Range.ofClosed(1, 2);
        Range heights = Range.ofClosed(1, 2);
        Range widths = Range.ofClosed(1, 2);

        Range kernelDepthRange = Range.ofClosed(1, 3);
        Range kernelHeightRange = Range.ofClosed(1, 3);
        Range kernelWidthRange = Range.ofClosed(1, 3);

        Range paddingDepthRange = Range.ofClosed(0, 3);
        Range paddingHeightRange = Range.ofClosed(0, 3);
        Range paddingWidthRange = Range.ofClosed(0, 3);

        Range strideDepthRange = Range.ofClosed(1, 3);
        Range strideHeightRange = Range.ofClosed(1, 3);
        Range strideWidthRange = Range.ofClosed(1, 3);

        Range dilationDepthRange = Range.ofClosed(1, 3);
        Range dilationHeightRange = Range.ofClosed(1, 3);
        Range dilationWidthRange = Range.ofClosed(1, 3);

        long rows =
                filters.size()
                        * depths.size()
                        * heights.size()
                        * widths.size()
                        * kernelDepthRange.size()
                        * kernelHeightRange.size()
                        * kernelWidthRange.size()
                        * paddingDepthRange.size()
                        * paddingHeightRange.size()
                        * paddingWidthRange.size()
                        * strideDepthRange.size()
                        * strideHeightRange.size()
                        * strideWidthRange.size()
                        * dilationDepthRange.size()
                        * dilationHeightRange.size()
                        * dilationWidthRange.size();

        IntStream.range(0, (int) rows)
                .mapToObj(TestData::new)
                .peek(
                        td -> {
                            long ix = td.getIndex();

                            Pair<Long, Long> option = Range.toValue(ix, filters);
                            td.setFilters(option.getKey().intValue() * 128);
                            ix = option.getValue();

                            option = Range.toValue(ix, depths);
                            td.setDepth(option.getKey().intValue() * 128);
                            ix = option.getValue();

                            option = Range.toValue(ix, heights);
                            td.setHeight(option.getKey().intValue() * 128);
                            ix = option.getValue();

                            option = Range.toValue(ix, widths);
                            td.setWidth(option.getKey().intValue() * 128);
                            ix = option.getValue();

                            Pair<Shape, Long> shaped =
                                    Range.toShape(
                                            ix,
                                            kernelDepthRange,
                                            kernelHeightRange,
                                            kernelWidthRange);
                            td.setKernel(shaped.getKey());
                            ix = shaped.getValue();

                            shaped =
                                    Range.toShape(
                                            ix,
                                            paddingDepthRange,
                                            paddingHeightRange,
                                            paddingWidthRange);
                            td.setPadding(shaped.getKey());
                            ix = shaped.getValue();

                            shaped =
                                    Range.toShape(
                                            ix,
                                            strideDepthRange,
                                            strideHeightRange,
                                            strideWidthRange);
                            td.setStride(shaped.getKey());
                            ix = shaped.getValue();

                            shaped =
                                    Range.toShape(
                                            ix,
                                            dilationDepthRange,
                                            dilationHeightRange,
                                            dilationWidthRange);
                            td.setDilation(shaped.getKey());
                        })
                .forEach(this::assertOutputShapes);
    }

    public void assertOutputShapes(TestData data) {
        Shape inputShape =
                new Shape(1, data.getFilters(), data.getDepth(), data.getHeight(), data.getWidth());
        long expectedDepth =
                ShapeUtils.convolutionDimensionCalculation(
                        data.getDepth(),
                        data.getKernel().get(0),
                        data.getPadding().get(0),
                        data.getStride().get(0),
                        data.getDilation().get(0));
        long expectedHeight =
                ShapeUtils.convolutionDimensionCalculation(
                        data.getHeight(),
                        data.getKernel().get(1),
                        data.getPadding().get(1),
                        data.getStride().get(1),
                        data.getDilation().get(1));
        long expectedWidth =
                ShapeUtils.convolutionDimensionCalculation(
                        data.getWidth(),
                        data.getKernel().get(2),
                        data.getPadding().get(2),
                        data.getStride().get(2),
                        data.getDilation().get(2));

        Conv3d.Builder builder =
                new Conv3d.Builder()
                        .setFilters(data.getFilters())
                        .setKernelShape(data.getKernel())
                        .optPadding(data.getPadding())
                        .optStride(data.getStride())
                        .optDilation(data.getDilation());

        Shape output = ShapeUtils.outputShapeForBlock(manager, builder.build(), inputShape);
        Assert.assertEquals(
                output,
                new Shape(1, data.getFilters(), expectedDepth, expectedHeight, expectedWidth));
    }
}
