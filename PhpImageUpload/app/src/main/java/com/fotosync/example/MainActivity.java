package com.fotosync.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	ProgressDialog prgDialog;
	String encodedString;
	RequestParams params = new RequestParams();
	String imgPath, fileName;
	Bitmap bitmap;
	private static int RESULT_LOAD_IMG = 1;
	int TAKE_PHOTO_CODE = 0, IMAGE_CAMERA = 0;
	private Uri outputFileUri;
	String file, currentDateandTime;
	TextView txtMensaje;
	ImageButton btnUpload;
	// GPSTracker class
	GPSTracker gps;
	ImageView imgView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		prgDialog = new ProgressDialog(this);
		// Set Cancelable as False
		prgDialog.setCancelable(false);

		imgView = (ImageView) findViewById(R.id.visor);
		final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String email=(mSharedPreference.getString("email", ""));
		String nombre=(mSharedPreference.getString("nombre", ""));
		//Log.e("A;LKJRQLE;JR;LEKRJT;LKE", "nombre: "+nombre+"...email: " +email);

		Toast.makeText( getApplicationContext(),"Bienvenid@ "+nombre, Toast.LENGTH_SHORT).show();

		params.put("email", email);


		// Creamos una carpeta llamada photoFolder para almacenar las
		// imagenes tomadas por la aplicacion
		final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/photoFolder/";
		File newdir = new File(dir);
		newdir.mkdirs();

		//Hacemos el match con XML
		ImageButton capture = (ImageButton) findViewById(R.id.btnCamera);
		btnUpload = (ImageButton) findViewById(R.id.btnUpload);
		txtMensaje = (TextView)findViewById(R.id.txtMensaje);
		btnUpload.setVisibility(View.GONE);


		capture.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Aqui, obtendremos la fecha y la hora del sistema para guardar
				// la foto con ese nombre, asi sera: fecha_hora.jpg
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd___HH_mm_ss");
				currentDateandTime = sdf.format(new Date());

				file = dir+currentDateandTime+".jpg";
				//System.out.println(file);

				File newfile = new File(file);
				try {
					newfile.createNewFile();
				}
				catch (IOException e)
				{
				}

				outputFileUri = Uri.fromFile(newfile);

				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

				startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
			}
		});
	}


	public void gps(){

		//GPS
		gps = new GPSTracker(MainActivity.this);

		// check if GPS enabled
		if(gps.canGetLocation()){

			double latitudes = gps.getLatitude();
			double longitudes = gps.getLongitude();

			// \n is for new line
			//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitudes + "\nLong: " + longitudes, Toast.LENGTH_LONG).show();
			//System.out.println("Latitud: "+latitudes+", Longitud: "+longitudes);

			String latitude = String.valueOf(latitudes);
			String longitude = String.valueOf(longitudes);

			params.put("latitude", latitude);
			params.put("longitude", longitude);

		}else{
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}

	}


	public void loadImagefromGallery(View view) {
		// Create intent to Open Image applications like Gallery, Google Photos
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// Start the Intent
		startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
	}

	// Cuando se selecciona la imagen de la galeria
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
				// Get the Image from data
				IMAGE_CAMERA = 0;

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgPath = cursor.getString(columnIndex);
				cursor.close();

				// Set the Image in ImageView
				imgView.setImageResource(0);
				imgView.setImageDrawable(null);
				imgView.setImageResource(android.R.color.transparent);
				imgView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
				// Get the Image's file name
				String fileNameSegments[] = imgPath.split("/");
				fileName = fileNameSegments[fileNameSegments.length - 1];
				System.out.print(""+fileName);
				// Put file name in Async Http Post Param which will used in Php web app
				params.put("filename", fileName);
				gps();

			} else if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) { //IMAGEN DE LA CAMARA
				IMAGE_CAMERA = 1;
				//Log.d("Path: ", "file> "+file);
				//Log.d("CameraDemo", "Pic saved");


				// Set the Image in ImageView
				Bitmap myBitmap = BitmapFactory.decodeFile(file);
				//Log.d("Bitmap", ""+myBitmap);
				imgView.setImageResource(0);
				imgView.setImageDrawable(null);
				imgView.setImageResource(android.R.color.transparent);
				imgView.setImageBitmap(myBitmap);


				params.put("filename", currentDateandTime);
				//System.out.println(""+currentDateandTime);
				gps();

			} else {
				Toast.makeText(this, "No has seleccionado una imagen",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
					.show();
		}

		txtMensaje.setVisibility(View.GONE);
		btnUpload.setVisibility(View.VISIBLE);
	}

	// When Upload button is clicked
	public void uploadImage(View v) {
		//System.out.println("Valor de file: "+file);
		// When Image is selected from Gallery
		if(IMAGE_CAMERA == 0){
			if (imgPath != null && !imgPath.isEmpty()) {
				prgDialog.setMessage("Converting Image to Binary Data");
				prgDialog.show();
				// Convert image to String using Base64
				encodeImagetoString();
				// When Image is not selected from Gallery
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Debes seleccionar una imagen de la galeria antes de intentar subirla",
						Toast.LENGTH_LONG).show();
			}
		} else {
			prgDialog.setMessage("Converting Image to Binary Data");
			prgDialog.show();
			// Convert image to String using Base64
			encodeImagetoString();
		}
	}

	// AsyncTask - To convert Image to String
	public void encodeImagetoString() {
		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {

			};

			@Override
			protected String doInBackground(Void... params) {
				BitmapFactory.Options options = null;
				options = new BitmapFactory.Options();
				options.inSampleSize = 3;

				if(IMAGE_CAMERA == 0){
					bitmap = BitmapFactory.decodeFile(imgPath, options);
				} else if (IMAGE_CAMERA == 1){
					bitmap = BitmapFactory.decodeFile(file,	options);
				} else {
					Log.e("RES: ","NO SE ASIGNO LA RUTA AL IMAGEVIEW");
				}


				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// Must compress the Image to reduce image size to make upload easy
				bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
				byte[] byte_arr = stream.toByteArray();
				// Encode Image to String
				encodedString = Base64.encodeToString(byte_arr, 0);
				return "";
			}

			@Override
			protected void onPostExecute(String msg) {
				prgDialog.setMessage("Calling Upload");
				// Put converted Image string into Async Http Post param
				params.put("image", encodedString);
				//Log.e("Resultado de params1: ",""+params);
				// Trigger Image upload
				triggerImageUpload();
			}
		}.execute(null, null, null);
	}

	public void triggerImageUpload() {
		makeHTTPCall();
	}

	// http://192.168.2.4:9000/imgupload/upload_image.php
	// http://192.168.2.4:9999/ImageUploadWebApp/uploadimg.jsp
	// Make Http call to upload Image to Php server
	public void makeHTTPCall() {
		prgDialog.setMessage("Haciendo referencia al PHP");
		AsyncHttpClient client = new AsyncHttpClient();
		// Don't forget to change the IP address to your LAN address. Port no as well.
		//client.post("http://10.43.2.89/fotosync/upload_image.php",
				client.post("http://172.16.11.70/fotosync/upload_image.php",
						//client.post("http://10.43.2.89/fotosync/upload_image.php",

				params, new AsyncHttpResponseHandler() {
					// When the response returned by REST has Http
					// response code '200'
					@Override
					public void onSuccess(String response) {
						// Hide Progress Dialog
						prgDialog.hide();
						Toast.makeText(getApplicationContext(), response,
								Toast.LENGTH_LONG).show();
						//Log.e("Resultado de params2: ",""+params);
					}

					// When the response returned by REST has Http
					// response code other than '200' such as '404',
					// '500' or '403' etc
					@Override
					public void onFailure(int statusCode, Throwable error,
										  String content) {
						// Hide Progress Dialog
						prgDialog.hide();
						// When Http response code is '404'
						if (statusCode == 404) {
							Toast.makeText(getApplicationContext(),
									"Requested resource not found",
									Toast.LENGTH_LONG).show();
						}
						// When Http response code is '500'
						else if (statusCode == 500) {
							Toast.makeText(getApplicationContext(),
									"Something went wrong at server end",
									Toast.LENGTH_LONG).show();
						}
						// When Http response code other than 404, 500
						else {
							Toast.makeText(
									getApplicationContext(),
									"Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
											+ statusCode, Toast.LENGTH_LONG)
									.show();
						}
					}
				});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Dismiss the progress bar when application is closed
		if (prgDialog != null) {
			prgDialog.dismiss();
		}
	}
}
