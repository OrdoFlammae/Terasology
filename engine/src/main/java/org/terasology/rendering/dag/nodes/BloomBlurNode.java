/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.rendering.dag.nodes;

import org.terasology.assets.ResourceUrn;
import org.terasology.config.Config;
import org.terasology.config.RenderingConfig;
import org.terasology.context.Context;
import org.terasology.registry.In;
import org.terasology.rendering.opengl.FBOConfig;
import org.terasology.rendering.opengl.fbms.DisplayResolutionDependentFBOs;



/**
 * If bloom is enabled via the rendering settings, this method generates the blurred images needed
 * for the bloom shader effect and stores them in their own frame buffers.
 * <p>
 * This effects renders adds fringes (or "feathers") of light to areas of intense brightness.
 * This in turn give the impression of those areas partially overwhelming the camera or the eye.
 * <p>
 * For more information see: http://en.wikipedia.org/wiki/Bloom_(shader_effect)
 */
public class BloomBlurNode extends BlurNode {
    public static final ResourceUrn HALF_SCALE_FBO = new ResourceUrn("engine:fbo.halfScaleBlurredBloom");
    public static final ResourceUrn QUARTER_SCALE_FBO = new ResourceUrn("engine:fbo.quarterScaleBlurredBloom");
    public static final ResourceUrn ONE_8TH_SCALE_FBO = new ResourceUrn("engine:fbo.oneEightScaleBlurredBloom");
    private static final float BLUR_RADIUS = 12.0f;

    /**
     * Constructs a BloomBlurNode instance. This method must be called once shortly after instantiation
     * to fully initialize the node and make it ready for rendering.
     *
     * @param inputConfig an FBOConfig describing the input FBO, to be retrieved from an injected DisplayResolutionDependentFBOs instance.
     * @param outputConfig an FBOConfig describing the output FBO, to be retrieved from an injected DisplayResolutionDependentFBOs instance.
     * @param aLabel a String to label the instance's entry in output generated by the PerformanceMonitor
     */
    public BloomBlurNode(Context context, FBOConfig inputConfig, FBOConfig outputConfig, String aLabel) {
        super(context, inputConfig, outputConfig, context.get(DisplayResolutionDependentFBOs.class), BLUR_RADIUS, aLabel);
    }

    /**
     * This method establishes the conditions in which the blur will take place, by enabling or disabling the node.
     *
     * In this particular case the node is enabled if RenderingConfig.isBloom is true.
     */
    @Override
    protected void setupConditions(Context context) {
        Config config = context.get(Config.class);
        RenderingConfig renderingConfig = config.getRendering();
        renderingConfig.subscribe(RenderingConfig.BLOOM, this);
        requiresCondition(renderingConfig::isBloom);
    }
}
