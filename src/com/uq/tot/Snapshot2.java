package com.uq.tot;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

public class Snapshot2 extends Activity {
	 private Camera mCamera;
	 private CamPreview2 mPreview;
    private static final String TAG ="ThisOrThat Snapshot2: ";
    private File picOne;
	private File picTwo;
	private Intent intent;
    Button btnCapture;
    TextView txtStatus;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	 
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	 
	        // Hide the window title.
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	 
	        setContentView(R.layout.cam_view2_main);
	 
	 
	        // Create an instance of Camera
	        mCamera = getCameraInstance();
	 
	        // Create our Preview view and set it as the content of our activity.
	        mPreview = new CamPreview2(this, mCamera);
	        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.cam_preview_2);
	        frameLayout.addView(mPreview);
	 
	        RelativeLayout relativeLayoutControls = (RelativeLayout) findViewById(R.id.controls_layout);
	        relativeLayoutControls.bringToFront();
	 
	        RelativeLayout relativeLayoutSensorsData = (RelativeLayout) findViewById(R.id.sensors_data_layout);
	        relativeLayoutSensorsData.bringToFront();
	        
	        txtStatus = (TextView) findViewById(R.id.camStatus2);
	       	intent = this.getIntent();
	       	
	    	addListenerOnButton();
	    	refreshStatusText();
	 
	    }
	    
	   public void logSensorData(String text)
	    {
	    	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	    }
	 
	    @Override
	    protected void onPause()
	    {
	    	super.onPause();
	    	releaseCamera();
	    	
	    }
	    
	    /** A safe way to get an instance of the Camera object. */
	    public static Camera getCameraInstance() {
	        Camera c = null;
	        try {
	            c = Camera.open();
	        }
	        catch (Exception e){
	            // Camera is not available (in use or does not exist)
	        }
	        return c; // returns null if camera is unavailable
	    }
	 
	    private void releaseCamera(){
	        if (mCamera != null){
	            mCamera.release();        // release the camera for other applications
	            mCamera = null;
	        }
	    }
	    
	    
	    public void addListenerOnButton(){
	    	btnCapture = (Button) findViewById(com.uq.tot.R.id.button_capture2);

	    	btnCapture.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					mCamera.takePicture(null, null, mPicture);
					btnCapture.setPressed(false);
					btnCapture.refreshDrawableState();
		

					
				}
	    		
	    		
	    	});
	    	
	    			
	    }
	    
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
		        	logSensorData(pictureFile.getAbsolutePath());
		        	intent.putExtra("FilePicOne", getPicOne());
		        }
		        else { // pic one is already taken, store as pic two 
		        	setPicTwo(pictureFile);
		        	logSensorData(R.string.camAction_pic_taken + " " + pictureFile.getAbsolutePath());
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
		
	    private void refreshStatusText(){
	    	if (getPicOne() == null && getPicTwo() == null){
	    		txtStatus.setText(R.string.camStatus_1pic);
	    	} else if (getPicTwo() == null) {
	    		txtStatus.setText(R.string.camStatus_2pic);
	    	} else if (getPicOne() != null && getPicTwo() != null) {
	    		txtStatus.setText(R.string.camStatus_done);
	    		setResult(Activity.RESULT_OK, intent);
	    		finish();
	    		
	    	} else {
	    		txtStatus.setText("This should not happen");
	    	}
	    }
	    
	    
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
