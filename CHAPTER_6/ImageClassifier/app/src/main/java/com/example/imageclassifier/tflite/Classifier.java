package com.example.imageclassifier.tflite;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Pair;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.label.TensorLabel;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class Classifier {
    private static final String MODEL_NAME = "mobilenet_imagenet_model.tflite";

    Context context;
    Interpreter interpreter = null;
    int modelInputWidth, modelInputHeight, modelInputChannel;
    int modelOutputClasses;

    public Classifier(Context context) {
        this.context = context;
    }

    public void init() throws IOException {
        interpreter = getInterpreter();
        initModelShape();
    }

    private ByteBuffer loadModelFile(String modelName) throws IOException {
        //org.tensorflow.lite.support.common.FileUtil에 구현되어있음
    //        org.tensorflow.lite.support.common.FileUtil.loadMappedFile(context, modelName);
        AssetManager am = context.getAssets();
        AssetFileDescriptor afd = am.openFd(modelName);
        FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
        FileChannel fc = fis.getChannel();
        long startOffset = afd.getStartOffset();
        long declaredLength = afd.getDeclaredLength();

        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private Interpreter getInterpreter() throws IOException {
        ByteBuffer model = loadModelFile(MODEL_NAME);
        model.order(ByteOrder.nativeOrder());
        return new Interpreter(model);
    }

    private void initModelShape() {
        Tensor inputTensor = interpreter.getInputTensor(0);
        int[] inputShape = inputTensor.shape();
        modelInputChannel = inputShape[0];
        modelInputWidth = inputShape[1];
        modelInputHeight = inputShape[2];

        Tensor outputTensor = interpreter.getOutputTensor(0);
        int[] outputShape = outputTensor.shape();
        modelOutputClasses = outputShape[1];
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, false);
    }

    private ByteBuffer convertBitmapToGrayByteBuffer(Bitmap bitmap) {
        ByteBuffer byteByffer = ByteBuffer.allocateDirect(bitmap.getByteCount());
        byteByffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixel : pixels) {
            int r = pixel >> 16 & 0xFF;
            int g = pixel >> 8 & 0xFF;
            int b = pixel & 0xFF;

            float avgPixelValue = (r + g + b) / 3.0f;
            float normalizedPixelValue = avgPixelValue / 255.0f;

            byteByffer.putFloat(normalizedPixelValue);
        }

        return byteByffer;
    }

    public Pair<Integer, Float> classify(Bitmap image) {
        ByteBuffer buffer = convertBitmapToGrayByteBuffer(resizeBitmap(image));

        float[][] result = new float[1][modelOutputClasses];

        interpreter.run(buffer, result);

        return argmax(result[0]);
    }


    private Pair<Integer, Float> argmax(float[] array) {
        int argmax = 0;
        float max = array[0];
        for(int i = 1; i < array.length; i++) {
            float f = array[i];
            if(f > max) {
                argmax = i;
                max = f;
            }
        }
        return new Pair<>(argmax, max);
    }

    public void finish() {
        if(interpreter != null)
            interpreter.close();
    }
}
