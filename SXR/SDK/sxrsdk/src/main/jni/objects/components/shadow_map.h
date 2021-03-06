/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


#ifndef SHADOW_MAP_H_
#define SHADOW_MAP_H_

#include "render_target.h"
#include "objects/textures/render_texture.h"

namespace sxr {
class Renderer;
class GLFrameBuffer;

    class ShadowMap : public RenderTarget
    {
    public:
        explicit ShadowMap(ShaderData* mtl);
        virtual void  beginRendering(Renderer* renderer);
        void setLayerIndex(int layerIndex);

        ShaderData* getShadowMaterial(){
            return mShadowMaterial;
        }

    protected:
        int         mLayerIndex;
        ShaderData* mShadowMaterial;
    };
}
#endif

