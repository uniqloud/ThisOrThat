package com.uq.tot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.*;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Snapshot extends Activity{
	
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	Camera mCam;
	CamPreview mPreview;
    Button btnCapture;
    private static final String TAG ="ThisOrThat Snapshot: ";
    private File picOne;
	private File picTwo;
	private Intent intent;
	private TextView txtResult;
	

    public File getPicOne() {
		return picOne;
	}

	public void setPicOne(File picOne) {
		this.picOne = picOne;
	}

	public File getPicTwo() {
		return picTwo;
	}

	public void setPicTwo(File picTwo) {
		this.picTwo = picTwo;
	}
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(com.uq.tot.R.layout.activity_snapshot);
//    	findViewById(com.uq.tot.R.id.camera_preview_one);
    	findViewById(com.uq.tot.R.id.snapshot_preview);

    	txtResult = (TextView) findViewById(com.uq.tot.R.id.resultText1);
		Log.d("Number of cameras: ",  Integer.toString(Camera.getNumberOfCameras()));
    	Log.d(TAG, "onCreate Snapshot");
    	addListenerOnButton();
    	refreshStatusText();
    	intent = this.getIntent();
    	//initiate camera
		if(safeCameraOpen(0)){
			mPreview = new CamPreview(this,mCam);
			SurfaceView preview = (SurfaceView) findViewById(com.uq.tot.R.id.camera_preview_one);
			//FrameLayout preview2 = (FrameLayout) findViewById(com.uq.tot.R.id.camera_preview_two);
			//preview.addView(mPreview);
		}
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    	Log.d(TAG, "onStart Snapshot");
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
   	Log.d(TAG, "onResume Snapshot");
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	releaseCamera();
    	Log.d(TAG, "onStop Snapshot");
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	releaseCamera();    	
    	Log.d(TAG, "onPause Snapshot");
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	Log.d(TAG, "onDestroy Snapshot");
    	releaseCameraAndPreview();
    	
    }
    
	
    private void refreshStatusText(){
    	if (getPicOne() == null && getPicTwo() == null){
    	txtResult.setText("0 pictures taken");
    	} else if (getPicTwo() == null) {
    		txtResult.setText("1 picture taken");
    	} else if (getPicOne() != null && getPicTwo() != null) {
    		txtResult.setText("2 pictures taken");
    	} else {
    		txtResult.setText("This should not happen");
    	}
    }
    
    private boolean safeCameraOpen(int id) {
	    boolean qOpened = false;
	  
	    try {
	        releaseCameraAndPreview();
	        mCam = Camera.open(id);
	        qOpened = (mCam != null);
	    } catch (Exception e) {
	        Log.e(TAG, "failed to open Camera");
	        e.printStackTrace();
	    }
	    // TODO check for camera features
	    // mCam.getParameters();

	    return qOpened;    
	}
	


	private void releaseCamera() {
	    if (mCam != null) {
	        mCam.release();
	        mCam = null;
	    }
	}
	

	private void releaseCameraAndPreview() {
	    if (mPreview != null){
		mPreview.setCamera(null);
	    }
	    if (mCam != null) {
	        mCam.release();
	        mCam = null;
	    }
	}
	
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
    public void addListenerOnButton(){
    	btnCapture = (Button) findViewById(com.uq.tot.R.id.button_capture2);

    	btnCapture.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "Imaged captured11!");
				mCam.takePicture(null, null, mPicture);
				Log.d(TAG, "Imaged captured22!");
				btnCapture.setPressed(false);
				btnCapture.refreshDrawableState();
	

				
			}
    		
    		
    	});
    	
    			
    }
	
	
	private class CamPreview extends SurfaceView implements SurfaceHolder.Callback {

	    private static final String TAG ="ThisOrThat: ";
		SurfaceHolder mHolder;
	    private Camera cam;

	    public CamPreview(Context context, Camera camera) {
	        super(context);
	        this.cam = camera;


	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();
	        mHolder.addCallback(this);
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }

		public void setCamera(Camera c) {
			mCam = c;
		}

		
		protected void resetCamPreview(){
			cam.stopPreview();
			cam.startPreview();
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			  // If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.

	        if (mHolder.getSurface() == null){
	          // preview surface does not exist
	          return;
	        }

	        // stop preview before making changes
	        try {
	            cam.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	        }

	        // set preview size and make any resize, rotate or
	        // reformatting changes here

	        // start preview with new settings
	        try {
	            cam.setPreviewDisplay(mHolder);
	            cam.startPreview();

	        } catch (Exception e){
	            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	        }
		}
	    
			

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try{
				cam.setPreviewDisplay(holder);
				cam.startPreview();
			}catch(IOException e){
				Log.d(TAG, "Error setting camera preview: " + e.getMessage());
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
		       // empty. Take care of releasing the Camera preview in your activity.
			
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			
	
			// TODO Auto-generated method stub
			
		}

	}
	
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	
	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d(TAG, "Error creating media file, check storage permissions: ");
	            return;
	        }
	        
	        FileOutputStream fos;
	        if (picOne == null){
	        	setPicOne(pictureFile);
	        	intent.putExtra("FilePicOne", getPicOne());
	        	
	        }
	        else { // pic one is already taken, store as pic two 
	        	setPicTwo(pictureFile);
	        	intent.putExtra("FilePicTwo", getPicTwo());
	        }
	        
	        try {
	            fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.getFD().sync();
	            fos.close();
	            Log.d(TAG, "File successfully written!!");
	        } 
	        catch (FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        }
	              
	        catch (IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	        
	  mPreview.resetCamPreview();
	  refreshStatusText();

	    }
	};
	
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		String sTotDir = "ThisOrThat";
		String baseDir = Environment.getExternalStorageDirectory() + File.separator + sTotDir;
		createDirIfNotExists (baseDir);
		
		File mediaStorageDir = new File (baseDir);
		
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    
	    File mediaFile;
	    String fileDir;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_TOT_"+ timeStamp + ".jpg");
	        fileDir = mediaStorageDir.getPath() + File.separator +
	    	        "IMG_TOT_"+ timeStamp + ".jpg";
	        Log.d(TAG, "File: " + fileDir);
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	   public static boolean createDirIfNotExists(String path) {
	        boolean ret = true;

	        File file = new File(Environment.getExternalStorageDirectory(), path);
	        if (!file.exists()) {
	            if (!file.mkdirs()) {
	                Log.e(TAG, "Problem creating folder " + path);
	                ret = false;
	            }
	        }
	        return ret;
	    }
	
}
