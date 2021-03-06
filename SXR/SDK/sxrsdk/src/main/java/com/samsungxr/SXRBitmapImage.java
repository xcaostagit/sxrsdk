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

package com.samsungxr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.samsungxr.asynchronous.SXRAsynchronousResourceLoader;
import com.samsungxr.utility.Log;

import java.io.IOException;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.List;

import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_LUMINANCE;


/**
 * Describes an uncompressed bitmap text
 * ure.
 * <p>
 * A bitmap texture contains 2D uncompressed pixel data in RGB
 * or RGBA format. This type of texture is inefficient because
 * it wastes GPU memory. Mobile GPUs can directly render from
 * compressed textures which use far less memory.
 * @see SXRCompressedImage
 */
public class SXRBitmapImage extends SXRImage
{
    /**
     * Constructs a texture using a pre-existing {@link Bitmap}.
     *
     * @param gvrContext
     *            Current {@link SXRContext}
     * @param bitmap
     *            A non-null {@link Bitmap} instance; do *not* call
     *            recycle on the bitmap
     */
    public SXRBitmapImage(SXRContext gvrContext, Bitmap bitmap)
    {
        super(gvrContext, NativeBitmapImage.constructor(ImageType.BITMAP.Value, bitmap.hasAlpha() ? GL_RGBA : GL_RGB));
        setBitmap(bitmap);
    }

    public SXRBitmapImage(SXRContext gvrContext)
    {
        super(gvrContext, NativeBitmapImage.constructor(ImageType.BITMAP.Value, GL_RGBA));
    }

    /**
     * Constructs a texture by loading a bitmap from a PNG file in (or under)
     * the {@code assets} directory.
     *
     * @param gvrContext
     *            Current {@link SXRContext}
     * @param assetFile
     *            The name of a texture file, relative to the assets
     *            directory. The assets directory may contain an arbitrarily
     *            complex tree of subdirectories; the file name can specify any
     *            location in or under the assets directory.
     */
    public SXRBitmapImage(SXRContext gvrContext, String assetFile) throws IOException
    {
        this(gvrContext);
        SXRAndroidResource resource = new SXRAndroidResource(gvrContext, assetFile);
        Bitmap bitmap = SXRAsynchronousResourceLoader.decodeStream(resource.getStream(), false);
        resource.closeStream();
        setFileName(assetFile);
        setBitmap(bitmap);
    }

    /**
     * Create a new, grayscale texture, from an array of luminance bytes.
     *
     * @param gvrContext
     *            Current {@link SXRContext}
     * @param width
     *            Texture width, in pixels
     * @param height
     *            Texture height, in pixels
     * @param grayscaleData
     *            {@code width * height} bytes of gray scale data
     *
     * @throws IllegalArgumentException
     *             If {@code width} or {@code height} is {@literal <= 0,} or if
     *             {@code grayScaleData} is {@code null}, or if
     *             {@code grayscaleData.length < height * width}
     */
    public SXRBitmapImage(SXRContext gvrContext, int width, int height, byte[] grayscaleData)
            throws IllegalArgumentException
    {
        super(gvrContext, NativeBitmapImage.constructor(ImageType.BITMAP.Value, GL_LUMINANCE));
        NativeBitmapImage.updateFromMemory(getNative(), width, height, grayscaleData);
    }

    /**
     * Copy a new {@link Bitmap} to the GPU texture. This one is also safe even
     * in a non-GL thread. An updateGPU request on a non-GL thread will
     * be forwarded to the GL thread and be executed before main rendering happens.
     *
     * Creating a new {@link SXRImage} is pretty cheap, but it's still not a
     * totally trivial operation: it does involve some memory management and
     * some GL hardware handshaking. Reusing the texture reduces this overhead
     * (primarily by delaying garbage collection). Do be aware that updating a
     * texture will affect any and all {@linkplain SXRMaterial materials}
     * (and/or post effects that use the texture!
     *
     * @param bmap  An Android Bitmap.
     *
     * @since 1.6.3
     */
    public void setBitmap(Bitmap bmap)
    {
        Bitmap.Config config = bmap.getConfig();

        //if a bitmap of format other than supported format is found,
        //try to create a new bitmap from it of config ARGB_8888.
        if (!supportedConfigs.contains(config))
            bmap = getBitmapSupported(bmap);

        NativeBitmapImage.updateFromBitmap(getNative(), bmap, bmap.hasAlpha(), bmap.getConfig().name());
    }

    private Bitmap getBitmapSupported(Bitmap orig)
    {
        Bitmap supBitmap = Bitmap.createBitmap( orig.getWidth(), orig.getHeight(), Bitmap.Config.ARGB_8888 );
        Canvas c = new Canvas(supBitmap);
        Paint p = new Paint();
        c.drawBitmap(orig,0,0,p);
        return supBitmap;
    }


    /**
     * Copy a new texture from a {@link Buffer} to the GPU texture. This one is also safe even
     * in a non-GL thread. An updateGPU request on a non-GL thread will
     * be forwarded to the GL thread and be executed before main rendering happens.
     *
     * Creating a new {@link SXRImage} is pretty cheap, but it's still not a
     * totally trivial operation: it does involve some memory management and
     * some GL hardware handshaking. Reusing the texture reduces this overhead
     * (primarily by delaying garbage collection). Do be aware that updating a
     * texture will affect any and all {@linkplain SXRMaterial materials}
     * (and/or post effects that use the texture!
     *
     * @param width
     *            Texture width, in texels
     * @param height
     *            Texture height, in texels
     * @param format
     *            Texture format
     * @param type
     *            Texture type
     * @param pixels
     *            A NIO Buffer with the texture
     *
     */
    public void setBuffer(final int width, final int height, final int format, final int type, final Buffer pixels)
    {
        NativeBitmapImage.updateFromBuffer(getNative(), 0, 0, width, height, format, type, pixels);
    }

    /**
     * Copy a new texture subimage from a {@link Buffer} to the GPU texture. This one is also safe even
     * in a non-GL thread. An updateGPU request on a non-GL thread will
     * be forwarded to the GL thread and be executed before main rendering happens.
     *
     * Creating a new {@link SXRImage} is pretty cheap, but it's still not a
     * totally trivial operation: it does involve some memory management and
     * some GL hardware handshaking. Reusing the texture reduces this overhead
     * (primarily by delaying garbage collection). Do be aware that updating a
     * texture will affect any and all {@linkplain SXRMaterial materials}
     * (and/or post effects that use the texture!
     *
     * @param xoffset
     *            Subimage texel offset in X direction
     * @param yoffset
     *            Subimage texel offset in Y direction
     * @param width
     *            Texture subimage width, in texels
     * @param height
     *            Texture subimage height, in texels
     * @param format
     *            Texture format
     * @param type
     *            Texture type
     * @param pixels
     *            A NIO Buffer with the texture
     *
     */
    public void setBuffer(final int xoffset, final int yoffset, final int width, final int height,
                          final int format, final int type, final Buffer pixels)
    {
        NativeBitmapImage.updateFromBuffer(getNative(), xoffset, yoffset, width, height, format, type, pixels);
    }

    /**
     * Copy new grayscale data to the GPU texture. This one is also safe even
     * in a non-GL thread. An updateGPU request on a non-GL thread will
     * be forwarded to the GL thread and be executed before main rendering happens.
     *
     * Be aware that updating a texture will affect any and all
     * {@linkplain SXRMaterial materials} and/or post effects that use the texture!
     * @param width     width of grayscale image
     * @param height    height of grayscale image
     * @param grayscaleData  A byte array containing grayscale data
     *
     * @since 1.6.3
     */
    public void update(int width, int height, byte[] grayscaleData)
    {
        NativeBitmapImage.updateFromMemory(getNative(), width, height, grayscaleData);
    }

    private static Bitmap loadBitmap(SXRContext gvrContext, String pngAssetFilename)
    {
        try
        {
            return BitmapFactory.decodeStream(
                    gvrContext.getContext().getAssets().open(pngAssetFilename));
        }
        catch (final IOException exc)
        {
            Log.e(TAG, "asset not found", exc);
        }
        return null;
    }

    private final static String TAG = "SXRBitmapTexture";
    private final static List<Bitmap.Config> supportedConfigs = Arrays.asList(
        Bitmap.Config.ALPHA_8,
        Bitmap.Config.ARGB_4444,
        Bitmap.Config.ARGB_8888,
        Bitmap.Config.RGB_565
    ) ;
}

class NativeBitmapImage {
    static native long constructor(int type, int format);
    static native void setFileName(long pointer, String fname);
    static native String getFileName(long pointer);
    static native void updateFromMemory(long pointer, int width, int height, byte[] data);
    static native void updateFromBitmap(long pointer, Bitmap bitmap, boolean hasAlpha, String format);
    static native void updateFromBuffer(long pointer, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels);
    static native void updateCompressed(long pointer, int width, int height, int imageSize, byte[] data, int levels, int[] offsets);

}