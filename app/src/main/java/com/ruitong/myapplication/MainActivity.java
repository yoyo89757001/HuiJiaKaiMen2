package com.ruitong.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.ruitong.myapplication.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback  {


    private Rect src = new Rect();
    private Rect dst = new Rect();
    Button paizhao;
    private String TAG = "paizhao";
    private Button baocun;

    private SurfaceView surfaceView;
    private Camera mCamera;
    private SurfaceHolder sh;

    private Bitmap bmp2 = null;
    private static boolean isTrue3 = true;
    private static boolean isTrue4 = false;
    private static int count = 1;
    private int cameraID=0;
    private String paths=null;

    private ImageView yulan;
    private RelativeLayout jiazai_rl;

    private String ss;
    private int dw,dh;
    private SurfaceHolder surfaceHolder;

    AFD_FSDKEngine engine = new AFD_FSDKEngine();
    AFD_FSDKVersion version = new AFD_FSDKVersion();
    List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
    AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jiazai_rl= (RelativeLayout) findViewById(R.id.jiazai_rl);
        yulan= (ImageView) findViewById(R.id.yulan);
        dw = Utils.getDisplaySize(MainActivity.this).x;
        dh = Utils.getDisplaySize(MainActivity.this).y;


        surfaceView = (SurfaceView) findViewById(R.id.fff);
        baocun= (Button) findViewById(R.id.baocu);
        baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("paizhang_sb");
                intent.putExtra("path",paths);
                sendBroadcast(intent);
                finish();
            }
        });



        Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());

        paizhao= (Button) findViewById(R.id.paizhao);
        paizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTrue4=true;
            }
        });



        OpenCameraAndSetSurfaceviewSize(cameraID);

    }


    private int getPreviewRotateDegree(int p) {
        int phoneDegree = 0;
        int result = 0;
        //获得手机方向
        int phoneRotate = getWindowManager().getDefaultDisplay().getOrientation();
        //得到手机的角度
        switch (phoneRotate) {
            case Surface.ROTATION_0:
                phoneDegree = 0;
                break;        //0
            case Surface.ROTATION_90:
                phoneDegree = 90;
                break;      //90
            case Surface.ROTATION_180:
                phoneDegree = 180;
                break;    //180
            case Surface.ROTATION_270:
                phoneDegree = 270;
                break;    //270
        }
        //分别计算前后置摄像头需要旋转的角度
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        if (p == 1) {
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
            result = (cameraInfo.orientation + phoneDegree) % 360;
            result = (360 - result) % 360;
        } else {
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
            result = (cameraInfo.orientation - phoneDegree + 360) % 360;
        }
        return result;
    }


    private void OpenCameraAndSetSurfaceviewSize(final int cameraId) {

        sh = surfaceView.getHolder();
        sh.addCallback(this);

        mCamera = Camera.open(cameraId);
        int rotateDegree = getPreviewRotateDegree(cameraId);
        mCamera.setDisplayOrientation(rotateDegree);

        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size pre_size = parameters.getPreviewSize();
        //  Camera.Size pic_size = parameters.getPictureSize();
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        try {
            mCamera.setPreviewDisplay(sh);
        } catch (IOException e) {
            e.printStackTrace();
        }



        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, final Camera camera) {

                if (isTrue4) {
                    isTrue4 = false;

                    try {
                        Camera.Size size = camera.getParameters().getPreviewSize();
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, stream);
                        bmp2 = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//                        //输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸跟踪返回结果保存在result。
//
//                        final Matrix matrix = new Matrix();
//                        if (cameraId == 1) {
//                            matrix.postRotate(270);
//                        } else {
//                            matrix.postRotate(90);
//                        }
//
//                        bmp2 = Bitmap.createBitmap(bmp2, 0, 0, bmp2.getWidth(), bmp2.getHeight(), matrix, true);

//                        String fn = "bbbb.jpg";
//                        FileUtil.isExists(FileUtil.PATH, fn);
//                        saveBitmap2File2(bmp2, FileUtil.SDPATH + File.separator + FileUtil.PATH + File.separator + fn, 100);


//                        byte[] data2 = new byte[bmp2.getWidth() * bmp2.getHeight()*3/2];
//                        ImageConverter convert = new ImageConverter();
//                        convert.initial(bmp2.getWidth(), bmp2.getHeight(), ImageConverter.CP_PAF_NV21);
//                        if (convert.convert(bmp2, data2)) {
//                            Log.d("com.arcsoft", "convert ok!");
//                        }
//                        convert.destroy();


                        err  = engine.AFD_FSDK_StillImageFaceDetection(data, bmp2.getWidth(), bmp2.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
                        Log.d("com.arcsoft", "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
                        if (result.size()>0){
                        Log.d("com.arcsoft", "AFD_FSDK_StillImageFaceDetection =" + result.get(result.size()-1).toString());
                        //销毁人脸跟踪引擎
                      //  err = engine.AFT_FSDK_UninitialFaceEngine();

                        while (sh != null) {
                            Canvas canvas = sh.lockCanvas();
                            if (canvas != null) {
                                Paint mPaint = new Paint();
                                boolean fit_horizontal = canvas.getWidth() / (float) src.width() < canvas.getHeight() / (float) src.height() ? true : false;
                                float scale = 1.0f;
                                if (fit_horizontal) {
                                    scale = canvas.getWidth() / (float) src.width();
                                    dst.left = 0;
                                    dst.top = (canvas.getHeight() - (int) (src.height() * scale)) / 2;
                                    dst.right = dst.left + canvas.getWidth();
                                    dst.bottom = dst.top + (int) (src.height() * scale);
                                } else {
                                    scale = canvas.getHeight() / (float) src.height();
                                    dst.left = (canvas.getWidth() - (int) (src.width() * scale)) / 2;
                                    dst.top = 0;
                                    dst.right = dst.left + (int) (src.width() * scale);
                                    dst.bottom = dst.top + canvas.getHeight();
                                }
//                                canvas.drawBitmap(mBitmap, src, dst, mPaint);
//                                canvas.save();
//                                canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
//                                canvas.translate(dst.left / scale, dst.top / scale);
                                for (AFD_FSDKFace face : result) {
                                    mPaint.setColor(Color.RED);
                                    mPaint.setStrokeWidth(10.0f);
                                    mPaint.setStyle(Paint.Style.STROKE);
                                    canvas.drawRect(face.getRect(), mPaint);
                                }
                                canvas.restore();
                                sh.unlockCanvasAndPost(canvas);


                                Log.d("com.arcsoft", "AFT_FSDK_UninitialFaceEngine =" + err.getCode());

                                //stream.close();
//                        Camera.Size size = camera.getParameters().getPreviewSize();
//                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
//
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, stream);
//
//                        bmp2 = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//
//                       // camera.stopPreview();
//
//                        final Matrix matrix = new Matrix();
//                        if (cameraId == 1) {
//                            matrix.postRotate(270);
//                        } else {
//                            matrix.postRotate(90);
//                        }
//
//                        bmp2 = Bitmap.createBitmap(bmp2, 0, 0, bmp2.getWidth(), bmp2.getHeight(), matrix, true);
//                      //  jiazai_rl.setVisibility(View.VISIBLE);
//                        yulan.setVisibility(View.VISIBLE);
//                        yulan.setImageBitmap(bmp2);
//                       // paizhao.setEnabled(false);
//
//
//
//                        stream.close();
                                isTrue4 = true;
                            }
                            }}
                    } catch(Exception ex){
                        Log.e("Sys", "Error:" + ex.getMessage());
                    }
                }
            }
        });

    }

    public void saveBitmap2File2(Bitmap bm, final String path, int quality) {
        try {
            paths=path;
            if (null == bm) {
                Log.d("InFoActivity", "回收|空");
                return;
            }

            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();


        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            if (!bm.isRecycled()) {
                bm.recycle();
            }
            bm = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mCamera.startPreview();
        surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d("PaiZhaoActivity", "改变");
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            // 获取摄像头支持的PictureSize列表
            List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
            for (Camera.Size size : pictureSizeList) {
                Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
            }
            /**从列表中选取合适的分辨率*/
            Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
            if (null == picSize) {
                //  Log.i(TAG, "null == picSize");
                picSize = parameters.getPictureSize();
            }
            //   Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
            // 根据选出的PictureSize重新设置SurfaceView大小
            float w = picSize.width;
            float h = picSize.height;
            parameters.setPictureSize(picSize.width, picSize.height);
            parameters.setPreviewFormat(ImageFormat.NV21);
            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams((int) (height * (h / w)), height));

            // 获取摄像头支持的PreviewSize列表
            List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

            for (Camera.Size size : previewSizeList) {
                Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
            }
            Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
            if (null != preSize) {
                // Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
                parameters.setPreviewSize(preSize.width, preSize.height);
            }

            parameters.setJpegQuality(100); // 设置照片质量
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
            }
            mCamera.cancelAutoFocus();//自动对焦。
            mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.d("InFoActivity3", "surfaceView销毁");

        if (mCamera != null) {
            sh.removeCallback(this);
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }



    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.qiehuan:
                isTrue4=true;
                //切换前后摄像头
                int cameraCount = 0;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

                for(int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                    if(cameraID == 0) {
                        //现在是后置，变更为前置
                        if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                            sh.removeCallback(this);
                            mCamera.setPreviewCallback(null);
                            mCamera.stopPreview();//停掉原来摄像头的预览
                            mCamera.release();//释放资源
                            mCamera = null;//取消原来摄像头
                            cameraID = 1;

                            OpenCameraAndSetSurfaceviewSize(cameraID);
                            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                            layoutParams.width=surfaceView.getWidth()-1;
                            layoutParams.height=surfaceView.getHeight()-1;
                            surfaceView.setLayoutParams(layoutParams);
                            surfaceView.invalidate();
                            mCamera.startPreview();

                            break;
                        }
                    } else {
                        isTrue4=true;
                        //现在是前置， 变更为后置
                        if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                            sh.removeCallback(this);
                            mCamera.setPreviewCallback(null);
                            mCamera.stopPreview();//停掉原来摄像头的预览
                            mCamera.release();//释放资源
                            mCamera = null;//取消原来摄像头
                            cameraID = 0;
                            OpenCameraAndSetSurfaceviewSize(cameraID);

                            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                            layoutParams.width=surfaceView.getWidth()-1;
                            layoutParams.height=surfaceView.getHeight()-1;
                            surfaceView.setLayoutParams(layoutParams);
                            surfaceView.invalidate();

                            mCamera.startPreview();

                            break;
                        }
                    }

                }


                break;

        }
    }


}
