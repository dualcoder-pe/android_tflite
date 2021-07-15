package com.example.imageclassifier.tflite;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.model.Model;

import java.io.IOException;

public class ClassifierBuilder {

    Context context;
    Model.Device device;
    int nThread;
    String modelName;
    float imageMean, imageStd, probabilityMean, probabilityStd;

    public ClassifierBuilder(Context context) {
        this.context = context;
        this.device = Model.Device.CPU;
        this.nThread = 1;

        this.modelName = "mobilenet_imagenet_model.tflite";
        this.imageMean = 0.0f;
        this.imageStd = 255.0f;
        this.probabilityMean = 0.0f;
        this.probabilityStd = 1.0f;
    }

    public ClassifierBuilder setDevice(Model.Device device) {
        this.device = device;
        return this;
    }

    public ClassifierBuilder setMultiThread(int nThread) {
        this.nThread = nThread;
        return this;
    }

    public ClassifierBuilder setModelName (String modelName) {
        this.modelName = modelName;
        return this;
    }

    public ClassifierBuilder setIntModelName(String modelName,
                             float imageMean, float imageStd,
                             float probabilityMean, float probabilityStd) {
        this.modelName = modelName;

        this.imageMean = imageMean;
        this.imageStd = imageStd;
        this.probabilityMean = probabilityMean;
        this.probabilityStd = probabilityStd;
        return this;
    }

    public Classifier build() throws IOException {
        Model model;

        switch(device) {
            case CPU: {
                model = createMultiThreadModel(nThread);
                break;
            }
            case GPU: {
                model = createGPUModel();
                break;
            }
            case NNAPI: {
                model = createNNAPIModel();
                break;
            }
            default:{
                model = createModel();
            }
        }

        return new Classifier(context, model) {
            @Override
            TensorOperator getPreprocessNormalizeOp() {
                return new NormalizeOp(imageMean, imageStd);
            }

            @Override
            TensorOperator getPostprocessNormalizeOp() {
                return new NormalizeOp(probabilityMean, probabilityStd);
            }
        };
    }


    private Model createModel() throws IOException {
        return Model.createModel(context, modelName);
    }

    private Model createMultiThreadModel(int nThreads) throws IOException {
        Model.Options.Builder optionsBuilder = new Model.Options.Builder();
        optionsBuilder.setNumThreads(nThreads);
        return Model.createModel(context, modelName, optionsBuilder.build());
    }

    private Model createGPUModel() throws IOException {
        Model.Options.Builder optionsBuilder = new Model.Options.Builder();
        CompatibilityList compatList = new CompatibilityList();

        // if the device has a supported GPU, add the GPU delegate
        if(compatList.isDelegateSupportedOnThisDevice()) {
            optionsBuilder.setDevice(Model.Device.GPU);
        } else {
            Toast.makeText(context, "Can't use GPU", Toast.LENGTH_SHORT).show();
        }
        return Model.createModel(context, modelName, optionsBuilder.build());
    }

    private Model createNNAPIModel() throws IOException {
        Model.Options.Builder optionsBuilder = new Model.Options.Builder();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            optionsBuilder.setDevice(Model.Device.NNAPI);
        } else {
            Toast.makeText(context, "Can't use NNAPI", Toast.LENGTH_SHORT).show();
        }

        return Model.createModel(context, modelName, optionsBuilder.build());
    }
}
