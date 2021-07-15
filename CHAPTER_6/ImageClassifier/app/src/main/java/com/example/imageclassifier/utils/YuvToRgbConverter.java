package com.example.imageclassifier.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import java.nio.ByteBuffer;

public class YuvToRgbConverter {

    public static void yuvToRgb(Context context, Image image, Bitmap output) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB scriptYuvToRgb =
                ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        int pixelCount = image.getCropRect().width() * image.getCropRect().height();
        int pixelSizeBits = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888);
        byte[] yuvBuffer = new byte[pixelCount * pixelSizeBits / 8];

        imageToByteArray(image, yuvBuffer, pixelCount);

        Type elemType = new Type.Builder(rs, Element.YUV(rs))
                .setYuvFormat(ImageFormat.NV21)
                .create();
        Allocation inputAllocation =
                Allocation.createSized(rs, elemType.getElement(), yuvBuffer.length);
        Allocation outputAllocation = Allocation.createFromBitmap(rs, output);

        inputAllocation.copyFrom(yuvBuffer);
        scriptYuvToRgb.setInput(inputAllocation);
        scriptYuvToRgb.forEach(outputAllocation);
        outputAllocation.copyTo(output);
    }

     private static void imageToByteArray(Image image, byte[] outputBuffer, int pixelCount) {
        assert image.getFormat() == ImageFormat.YUV_420_888;

        Rect imageCrop = image.getCropRect();
        Image.Plane[] imagePlanes = image.getPlanes();

        for(int planeIndex = 0; planeIndex < imagePlanes.length; planeIndex++) {
            Image.Plane plane = imagePlanes[planeIndex];
            int outputStride;

            int outputOffset;

            switch (planeIndex) {
                case 0: {
                    outputStride = 1;
                    outputOffset = 0;
                    break;
                }
                case 1: {
                    outputStride = 2;
                    outputOffset = pixelCount + 1;

                    break;
                }
                case 2: {
                    outputStride = 2;
                    outputOffset = pixelCount;
                    break;
                }
                default: {
                    return;
                }
            }

            ByteBuffer planeBuffer = plane.getBuffer();
            int rowStride = plane.getRowStride();
            int pixelStride = plane.getPixelStride();

            Rect planeCrop;
            if (planeIndex == 0) {
                planeCrop = imageCrop;
            } else {
                planeCrop = new Rect(
                        imageCrop.left / 2,
                        imageCrop.top / 2,
                        imageCrop.right / 2,
                        imageCrop.bottom / 2
                );
            }

            int planeWidth = planeCrop.width();
            int planeHeight = planeCrop.height();

            byte[] rowBuffer = new byte[plane.getRowStride()];

            int rowLength;
            if (pixelStride == 1 && outputStride == 1) {
                rowLength = planeWidth;
            } else {
                rowLength = (planeWidth - 1) * pixelStride + 1;
            }

            for(int row = 0; row < planeHeight; row++){
                planeBuffer.position(
                        (row + planeCrop.top) * rowStride + planeCrop.left * pixelStride);

                if (pixelStride == 1 && outputStride == 1) {
                    planeBuffer.get(outputBuffer, outputOffset, rowLength);
                    outputOffset += rowLength;
                } else {
                    planeBuffer.get(rowBuffer, 0, rowLength);
                    for (int col = 0; col < planeWidth; col++) {
                        outputBuffer[outputOffset] = rowBuffer[col * pixelStride];
                        outputOffset += outputStride;
                    }
                }
            }
        }
    }
}