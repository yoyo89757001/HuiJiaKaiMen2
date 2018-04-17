package com.ruitong.myapplication;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;

import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;
import com.ruitong.myapplication.beans.MenBean;
import com.ruitong.myapplication.cookies.CookiesManager;
import com.ruitong.myapplication.interfaces.RecytviewCash;
import com.ruitong.myapplication.utils.GlideCircleTransform;
import com.ruitong.myapplication.utils.GsonUtil;
import com.ruitong.myapplication.utils.Utils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static java.security.AccessController.getContext;

/**
 * Created by gqj3375 on 2017/4/28.
 */

public class DetecterActivity extends Activity implements OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback , RecytviewCash {
	private final String TAG = this.getClass().getSimpleName();

	private int mWidth, mHeight, mFormat;
	private CameraSurfaceView mSurfaceView;
	private CameraGLSurfaceView mGLSurfaceView;
	private Camera mCamera;

	AFT_FSDKVersion version = new AFT_FSDKVersion();
	AFT_FSDKEngine engine = new AFT_FSDKEngine();
	ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
	ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();
	ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
	ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();
	List<AFT_FSDKFace> result = new ArrayList<>();
	List<AFT_FSDKFace> result2 = new ArrayList<>();
	List<ASAE_FSDKAge> ages = new ArrayList<>();
	List<ASGE_FSDKGender> genders = new ArrayList<>();

	private int dw,dh;
	private OkHttpClient okHttpClient=null;
	int mCameraID;
	int mCameraRotate;
	boolean mCameraMirror;
	byte[] mImageNV21 = null;
	FRAbsLoop mFRAbsLoop = null;
	AFT_FSDKFace mAFT_FSDKFace = null;
	Handler mHandler;
	private RecyclerView recyclerView2;
	private WrapContentLinearLayoutManager manager2;
	private BlockingQueue<String> basket = new LinkedBlockingQueue<String>(5);
	private static Vector<MenBean> menBeansList=new Vector<>();

	private static boolean isA=true;
	private static int  faceSize=1;
	private ImagesAdapter adapter;
	private ShowAdapter showAdapter;

	private static Vector<Bitmap> bitmapList=new Vector<>();

	private final int TIMEOUT=1000*30;
	private  String screen_token=null;



	public  Handler handler=new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(final Message msg) {
			switch (msg.what) {
//				case 111:
//					//更新地址
//
//					break;
//				case 110:
//					if (lingdaoList.size() > 1) {
//
////						AnimatorSet animatorSet = new AnimatorSet();
////						animatorSet.playTogether(
////								ObjectAnimator.ofFloat(adapter2.getViewByPosition(recyclerView2, 1, R.id.ffflll), "scaleY", 1f, 0f),
////								ObjectAnimator.ofFloat(adapter2.getViewByPosition(recyclerView2, 1, R.id.ffflll), "scaleX", 1f, 0f)
////								//	ObjectAnimator.ofFloat(helper.itemView,"alpha",0f,1f)
////						);
////						animatorSet.setDuration(200);
////						animatorSet.setInterpolator(new AccelerateInterpolator());
////						animatorSet.addListener(new AnimatorListenerAdapter() {
////							@Override
////							public void onAnimationEnd(Animator animation) {
////								adapter2.notifyItemRemoved(1);
////								lingdaoList.remove(1);
////
////							}
////						});
////						animatorSet.start();
//
//					}
//
//
//					break;
				case 999:

					if (menBeansList.size()>0){
						showAdapter.notifyItemRemoved(0);
						menBeansList.remove(0);
					}



					break;


			}

			if (msg.arg1==1) {
				MenBean dataBean = (MenBean) msg.obj;
				try {

						switch (dataBean.getPerson().getTag().getSubject_type()) {
							case 0: //员工
								//Log.d(TAG, "员工k");
								int a = 0;
								for (int i2 = 0; i2 < menBeansList.size(); i2++) {
									if (Objects.equals(menBeansList.get(i2).getPerson().getTag().getId(), dataBean.getPerson().getTag().getId())) {
										a = 1;
									}
								}

								if (a == 0) {
									menBeansList.add(dataBean);
									int i1 = menBeansList.size();
									adapter.notifyItemInserted(i1);
									manager2.scrollToPosition(i1 - 1);



									new Thread(new Runnable() {
										@Override
										public void run() {

											try {

												SystemClock.sleep(8000);
												Message message = Message.obtain();
												message.what = 999;
												handler.sendMessage(message);

											} catch (Exception e) {
												e.printStackTrace();
											}


										}
									}).start();

								}
								break;

//						case 1: //普通访客
//							yuangongList.add(bean);
//							int i2=yuangongList.size();
//							adapter.notifyItemInserted(i2);
//							manager.scrollToPosition(i2-1);
//							new Thread(new Runnable() {
//								@Override
//								public void run() {
//
//									try {
//										Thread.sleep(10000);
//
//										Message message=Message.obtain();
//										message.what=999;
//										handler.sendMessage(message);
//
//									} catch (InterruptedException e) {
//										e.printStackTrace();
//									}
//
//
//								}
//							}).start();
//
//							break;
//						case 2:  //VIP访客
//							yuangongList.add(bean);
//							int i3=yuangongList.size();
//							adapter.notifyItemInserted(i3);
//							manager.scrollToPosition(i3-1);
//
//							new Thread(new Runnable() {
//								@Override
//								public void run() {
//
//									try {
//										Thread.sleep(10000);
//										Message message=Message.obtain();
//										message.what=999;
//										handler.sendMessage(message);
//
//									} catch (InterruptedException e) {
//										e.printStackTrace();
//									}
//
//
//								}
//							}).start();
//
//
//							break;

						}
				} catch (Exception e) {
					//Log.d("WebsocketPushMsg", e.getMessage());
					e.printStackTrace();
				}

			}
			//else if (msg.arg1==2) {

//			final WeiShiBieBean dataBean = (WeiShiBieBean) msg.obj;
//
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//
//						try {
//
//							BASE64Decoder decoder = new BASE64Decoder();
//							// Base64解码
//							final byte[][] b;
//
//							b = new byte[][]{decoder.decodeBuffer(dataBean.getFace().getImage())};
//							for (int i = 0; i < b[0].length; ++i) {
//								if (b[0][i] < 0) {// 调整异常数据
//									b[0][i] += 256;
//								}
//							}
//
//							TanChuangBean bean = new TanChuangBean();
//							bean.setBytes(b[0]);
//							bean.setName("陌生人");
//							bean.setType(-1);
//							bean.setTouxiang(null);
//							yuangongList.add(bean);
//							final int i3=yuangongList.size();
//							runOnUiThread(new Runnable() {
//								@Override
//								public void run() {
//
//									adapter.notifyItemInserted(i3);
//									manager.scrollToPosition(i3 - 1);
//								}
//							});
//
//							Thread.sleep(10000);
//
//							Message message = Message.obtain();
//							message.what = 999;
//							handler.sendMessage(message);
//
//
//						} catch (Exception e) {
//
//							Log.d(TAG, e.getMessage() + "陌生人解码");
//						}
//
//					}
//				}).start();
//			}

			return false;
		}
	});




	@Override
	public void reset() {

	}

	class FRAbsLoop extends AbsLoop {

		AFR_FSDKVersion version = new AFR_FSDKVersion();
		AFR_FSDKEngine engine = new AFR_FSDKEngine();
		AFR_FSDKFace result = new AFR_FSDKFace();
		List<FaceDB.FaceRegist> mResgist = ((Application)DetecterActivity.this.getApplicationContext()).mFaceDB.mRegister;
		List<ASAE_FSDKFace> face1 = new ArrayList<>();
		List<ASGE_FSDKFace> face2 = new ArrayList<>();
		
		@Override
		public void setup() {
			AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
			Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
			error = engine.AFR_FSDK_GetVersion(version);
			Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
		}

		@Override
		public void loop() {

//			if (mImageNV21 != null) {
//				long time = System.currentTimeMillis();
//				AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
//				Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
//				Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
//				AFR_FSDKMatching score = new AFR_FSDKMatching();
//				float max = 0.0f;
//				String name = null;
//				for (FaceDB.FaceRegist fr : mResgist) {
//					for (AFR_FSDKFace face : fr.mFaceList) {
//						error = engine.AFR_FSDK_FacePairMatching(result, face, score);
//						Log.d(TAG,  "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
//						if (max < score.getScore()) {
//							max = score.getScore();
//							name = fr.mName;
//						}
//					}
//				}
//				//age & gender
//				face1.clear();
//				face2.clear();
//				face1.add(new ASAE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
//				face2.add(new ASGE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
//				ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
//				ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
//				Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + error1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + error2.getCode());
//				Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
//				final String age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
//				final String gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");
//
//				//crop
//				byte[] data = mImageNV21;
//				YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
//				ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
//				yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 100, ops);
//				final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
//				try {
//					ops.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				if (max > 0.6f) {
//					//fr success.
//					final float max_score = max;
//					Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
//					final String mNameShow = name;
//					mHandler.removeCallbacks(hide);
//					mHandler.post(new Runnable() {
//						@Override
//						public void run() {
//
//							mTextView.setAlpha(1.0f);
//							mTextView.setText(mNameShow);
//							mTextView.setTextColor(Color.RED);
//							mTextView1.setVisibility(View.VISIBLE);
//							mTextView1.setText("置信度：" + (float)((int)(max_score * 1000)) / 1000.0);
//							mTextView1.setTextColor(Color.RED);
//							mImageView.setRotation(mCameraRotate);
//							if (mCameraMirror) {
//								mImageView.setScaleY(-1);
//							}
//							mImageView.setImageAlpha(255);
//							mImageView.setImageBitmap(bmp);
//						}
//					});
//				} else {
//					final String mNameShow = "未识别";
//					DetecterActivity.this.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							mTextView.setAlpha(1.0f);
//							mTextView1.setVisibility(View.VISIBLE);
//							mTextView1.setText( gender + "," + age);
//							mTextView1.setTextColor(Color.RED);
//							mTextView.setText(mNameShow);
//							mTextView.setTextColor(Color.RED);
//							mImageView.setImageAlpha(255);
//							mImageView.setRotation(mCameraRotate);
//							if (mCameraMirror) {
//								mImageView.setScaleY(-1);
//							}
//							mImageView.setImageBitmap(bmp);
//						}
//					});
//				}
//				mImageNV21 = null;
//			}

		}

		@Override
		public void over() {
			AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
			Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
		}
	}

	private RecyclerView recyclerView;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getOkHttpClient2();
		mCameraID = getIntent().getIntExtra("Camera", 0) == 0 ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
		mCameraRotate = getIntent().getIntExtra("Camera", 0) == 0 ? 90 : 270;
		mCameraMirror = getIntent().getIntExtra("Camera", 0) != 0;


		mFormat = ImageFormat.NV21;
		mHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {

				return false;
			}
		});

		dw = Utils.getDisplaySize(DetecterActivity.this).x;
		dh = Utils.getDisplaySize(DetecterActivity.this).y;

		setContentView(R.layout.activity_camera);
		mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
		mGLSurfaceView.setOnTouchListener(this);
		mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
		mSurfaceView.setOnCameraListener(this);
		mSurfaceView.debug_print_fps(false, false);

		adapter=new ImagesAdapter(bitmapList);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(DetecterActivity.this, LinearLayoutManager.VERTICAL, false,this);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setAdapter(adapter);

		recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        manager2 = new WrapContentLinearLayoutManager(DetecterActivity.this,LinearLayoutManager.HORIZONTAL,false,this);
        recyclerView2.setLayoutManager(manager2);

		showAdapter = new ShowAdapter(menBeansList);
		recyclerView2.setAdapter(showAdapter);

		AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
		Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
		err = engine.AFT_FSDK_GetVersion(version);
		Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

		ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
		Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + error.getCode());
		error = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);
		Log.d(TAG, "ASAE_FSDK_GetVersion:" + mAgeVersion.toString() + "," + error.getCode());

		ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
		Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + error1.getCode());
		error1 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);
		Log.d(TAG, "ASGE_FSDK_GetVersion:" + mGenderVersion.toString() + "," + error1.getCode());

		RelativeLayout.LayoutParams  params1= (RelativeLayout.LayoutParams) recyclerView2.getLayoutParams();
		params1.height=dh*2/3;
		recyclerView2.setLayoutParams(params1);
		recyclerView2.invalidate();

		mFRAbsLoop = new FRAbsLoop();
		mFRAbsLoop.start();
	}


	public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {
		private List<MenBean> datas;

//    private ClickIntface clickIntface;
//    public void setClickIntface(ClickIntface clickIntface){
//        this.clickIntface=clickIntface;
//    }

		private ShowAdapter(List<MenBean> datas) {
			this.datas = datas;
		}
		//创建新View，被LayoutManager所调用
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tanchuang_item6,viewGroup,false);
			return new ViewHolder(view);
		}
		//将数据与界面进行绑定的操作
		@Override
		public void onBindViewHolder(ShowAdapter.ViewHolder viewHolder, final int position) {
				switch (datas.get(position).getPerson().getTag().getSubject_type()){
					case 0:
						//员工
						viewHolder.name.setText(datas.get(position).getPerson().getTag().getName());
						viewHolder.zhuangtai.setText("员工");
						viewHolder.bg.setBackgroundResource(R.drawable.pufa_ld);
						Glide.with(Application.getContext())
								//.load(zhuji+item.getTouxiang())
								.load("http://192.168.2.64"+datas.get(position).getPerson().getTag().getAvatar())
								//.apply(myOptions2)
								.transform(new GlideCircleTransform(Application.getContext(),2,Color.parseColor("#ffffffff")))
								//	.transform(new GlideRoundTransform(MyApplication.getAppContext(), 6))
								.into(viewHolder.touxiang);
						RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.bg.getLayoutParams();
						lp.leftMargin=10;
						lp.width=dw/3-10;
						viewHolder.bg.setLayoutParams(lp);
						viewHolder.bg.invalidate();

						break;

				}



		}
		//获取数据的数量
		@Override
		public int getItemCount() {
			return datas.size();
		}
		//自定义的ViewHolder，持有每个Item的的所有界面元素
		class ViewHolder extends RecyclerView.ViewHolder {
			private ImageView touxiang;
			private TextView name,zhuangtai;
			private RelativeLayout bg;

			private ViewHolder(View view){
				super(view);
				touxiang = (ImageView) view.findViewById(R.id.touxiang);
				name = (TextView) view.findViewById(R.id.name33);
				zhuangtai = (TextView) view.findViewById(R.id.zhuangtai33);
				bg=(RelativeLayout)view.findViewById(R.id.ffflll);


			}
		}


	}


	@Override
	protected void onStop() {
		isA=true;
		faceSize=1;
		super.onStop();

	}

	/* (non-Javadoc)
         * @see android.app.Activity#onDestroy()
         */
	@Override
	protected void onDestroy() {

		super.onDestroy();
		mFRAbsLoop.shutdown();
		AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
		Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());
		ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
		Log.d(TAG, "ASAE_FSDK_UninitAgeEngine =" + err1.getCode());
		ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
		Log.d(TAG, "ASGE_FSDK_UninitGenderEngine =" + err2.getCode());
	}

	@Override
	public Camera setupCamera() {
		try {
			mCamera = Camera.open(1);
			int rotateDegree = getPreviewRotateDegree(1);
			mCamera.setDisplayOrientation(rotateDegree);
			mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, getPreviewRotateDegree(1));

			int PreviewWidth = 0;
			int PreviewHeight = 0;
			Camera.Parameters parameters  = mCamera.getParameters();
			// 选择合适的预览尺寸
			List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
			// 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
			if (sizeList.size() > 1) {
				for (Camera.Size cur : sizeList) {
					if (cur.width >= PreviewWidth
							&& cur.height >= PreviewHeight) {
						PreviewWidth = cur.width;
						PreviewHeight = cur.height;
						break;
					}
				}
			}
			parameters.setPreviewSize(PreviewWidth, PreviewHeight); //获得摄像区域的大小
			parameters.setPictureSize(PreviewWidth, PreviewHeight);//设置拍出来的屏幕大小
			//parameters.setPreviewSize(mWidth, mHeight);
			parameters.setPreviewFormat(mFormat);

			for( Camera.Size size : parameters.getSupportedPreviewSizes()) {
				Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
			}
			for( Integer format : parameters.getSupportedPreviewFormats()) {
				Log.d(TAG, "FORMAT:" + format);
			}

			List<int[]> fps = parameters.getSupportedPreviewFpsRange();
			for(int[] count : fps) {
				Log.d(TAG, "T:");
				for (int data : count) {
					Log.d(TAG, "V=" + data);
				}
			}

			//parameters.setPreviewFpsRange(15000, 30000);
			//parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
			//parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
			//parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
			//parmeters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			//parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			//parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);

			mCamera.setParameters(parameters);

		} catch (Exception e) {
			Log.d("fffffffff", e.getMessage()+"fff");
			if (mCamera!=null)
			mCamera.release();
			SystemClock.sleep(200);
			mCamera = Camera.open(0);
			int rotateDegree = getPreviewRotateDegree(0);
			mCamera.setDisplayOrientation(rotateDegree);
			mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, getPreviewRotateDegree(0));

		}
		if (mCamera != null) {
			mWidth = mCamera.getParameters().getPreviewSize().width;
			mHeight = mCamera.getParameters().getPreviewSize().height;
		}
		return mCamera;
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


	@Override
	public void setupChanged(int format, int width, int height) {

	}

	@Override
	public boolean startPreviewLater() {

		return false;
	}

	@Override
	public Object onPreview(final byte[] data, final int width, final int height, int format, long timestamp) {
//		Log.d("DetecterActivity", "width:" + width);
//		Log.d("DetecterActivity", "height:" + height);
	//	Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
	//	Log.d(TAG, "Face=" + result.size());
//		for (AFT_FSDKFace face : result) {
//			Log.d(TAG, "Face:" + face.toString());
//		}
		AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);

		if (isA) {
			isA=false;
			final int size=result.size();
			if (!result.isEmpty()) {
					if (bitmapList.size()>0){
						bitmapList.clear();
						adapter.notifyDataSetChanged();
					}
					for (AFT_FSDKFace fsdkFace : result){
						YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
						ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
						yuv.compressToJpeg(fsdkFace.getRect(), 100, ops);
						final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
						bitmapList.add(bmp);
						adapter.notifyDataSetChanged();
						try {
							basket.put("An apple");
							Log.d("hhhhhh", "插入成功");
						} catch (InterruptedException e) {
							Log.d("hhhhhh", e.getMessage()+"插入异常");
							basket.clear();
							isA=true;
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								link_P2(compressImage(bmp),size);
							}
						}).start();

						try {
							ops.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}


					}else {

						isA=true;
						if (bitmapList.size()>0){
							bitmapList.clear();
							adapter.notifyDataSetChanged();
						}

					}



		}

		//copy rects
		Rect[] rects = new Rect[result.size()];
		for (int i = 0; i < result.size(); i++) {
			rects[i] = new Rect(result.get(i).getRect());
		}
		//clear result.
		result.clear();
		//return the rects for render.
		return rects;
	}

	@Override
	public void onBeforeRender(CameraFrameData data) {

	}

	@Override
	public void onAfterRender(CameraFrameData data) {

		mGLSurfaceView.getGLES2Render().draw_rect((Rect[])data.getParams(), Color.GREEN, 2);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		CameraHelper.touchFocus(mCamera, event, v, this);
		return false;
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (success) {
			Log.d(TAG, "Camera Focus SUCCESS!");
		}
	}


	//首先登录-->获取所有主机-->创建或者删除或者更新门禁
	private void getOkHttpClient2(){
		okHttpClient = new OkHttpClient.Builder()
				.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.cookieJar(new CookiesManager())
				.retryOnConnectionFailure(true)
				.build();

		RequestBody body = new FormBody.Builder()
				.add("username", "test@megvii.com")
				.add("password", "123456")
                .add("pad_id", Utils.getIMSI())
                .add("device_type", "2")
				.build();

		Request.Builder requestBuilder = new Request.Builder();
		requestBuilder.header("User-Agent", "Koala Admin");
		requestBuilder.header("Content-Type","application/json");
		requestBuilder.post(body);
		requestBuilder.url("http://192.168.2.64"+"/pad/login");
		final Request request = requestBuilder.build();

		Call mcall= okHttpClient.newCall(request);
		mcall.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d(TAG, "登陆失败"+e.getMessage());
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String s=response.body().string();
				Log.d(TAG, "登录"+s);
				JsonObject jsonObject= GsonUtil.parse(s).getAsJsonObject();
				int n=1;
				n=jsonObject.get("code").getAsInt();
				if (n==0){
					//登录成功,后续的连接操作因为cookies 原因,要用 MyApplication.okHttpClient
					JsonObject jo=jsonObject.get("data").getAsJsonObject();
					screen_token=jo.get("screen_token").getAsString();
					Log.d("DetecterActivity", screen_token);
				}
				else {

				}
			}
		});

	}


	public static final int TIMEOUT2 = 1000 * 5;
	// 1:N 对比
	private void link_P2(final File file, final int size) {
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.writeTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
				.connectTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
				.readTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
				.cookieJar(new CookiesManager())
				.retryOnConnectionFailure(true)
				.build();
		;
		MultipartBody mBody;
		final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

		RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream"),file);

		builder.addFormDataPart("image",file.getName(), fileBody1);
		builder.addFormDataPart("screen_token",screen_token==null?"":screen_token);
		mBody = builder.build();

		Request.Builder requestBuilder = new Request.Builder()
				.header("Content-Type", "application/json")
				.post(mBody)
				.url("http://192.168.2.64"+ ":8866/recognize");

		// step 3：创建 Call 对象
		Call call = okHttpClient.newCall(requestBuilder.build());

		//step 4: 开始异步请求
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("AllConnects", "请求识别失败" + e.getMessage());
				SystemClock.sleep(300);
				try {
					basket.take();
					if (basket.size()==0)
						isA=true;
				} catch (InterruptedException e1) {
					basket.clear();
					isA=true;
					e1.printStackTrace();
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

				Log.d("AllConnects", "请求识别成功" + call.request().toString()+file.delete());
				//获得返回体
				try {
					ResponseBody body = response.body();
					String ss = body.string();
					Log.d("AllConnects", "传照片" + ss);
					String s2=ss.replace("\\\\u","@!@#u").replace("\\","")
							.replace("tag\": \"{","tag\":{")
							.replace("jpg\"}\"","jpg\"}")
							.replace("@!@#","\\");


					Log.d("AllConnects", "传照片2" + s2);

					JsonObject jsonObject = GsonUtil.parse(s2).getAsJsonObject();
					Gson gson = new Gson();
					MenBean menBean = gson.fromJson(jsonObject, MenBean.class);
					if (menBean.isCan_door_open()){

						Message message=Message.obtain();
						message.arg1=1;
						message.obj=menBean;
						handler.sendMessage(message);
					}

					Log.d("DetecterActivity", "menBean:" + menBean.isCan_door_open());

				} catch (Exception e) {
					Log.d("WebsocketPushMsg", e.getMessage()+"");
				}finally {
					SystemClock.sleep(300);
					try {
						basket.take();
						if (basket.size()==0)
							isA=true;
					} catch (InterruptedException e1) {
						basket.clear();
						isA=true;
						e1.printStackTrace();
					}
				}

			}
		});
	}


	/**
	 * 压缩图片（质量压缩）
	 * @param bitmap
	 */
	public static File compressImage(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 300) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			//long length = baos.toByteArray().length;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date(System.currentTimeMillis());
		String filename = format.format(date);
		File file = new File(Environment.getExternalStorageDirectory(),filename+".png");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			try {
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		//	recycleBitmap(bitmap);
		return file;
	}
}
