package com.taxiappclone.common.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.taxiappclone.common.R;
import com.taxiappclone.common.app.AppConfig;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.android.volley.VolleyLog.TAG;
import static com.taxiappclone.common.app.AppConfig.REQUEST_CAMERA;
import static com.taxiappclone.common.app.AppConfig.SELECT_FILE;
import static com.taxiappclone.common.app.CommonUtils.bitmapToString;
import static com.taxiappclone.common.app.CommonUtils.setStatusBarGradient;

public class SelectPhotoActivity extends CustomActivity {
    private static final String TAG = SelectPhotoActivity.class.getSimpleName();
    private Bitmap bitmap;
    private String imageString;
    public static final String FREE_SIZE_IMAGE = "free_size_image";
    private boolean freeSizeImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(FREE_SIZE_IMAGE)) {
            freeSizeImage = true;
        }
        if (getIntent().hasExtra("type")) {
            if (getIntent().getStringExtra("type").equals("camera")) {
                cameraIntent();
            } else {
                galleryIntent();
            }
        } else {
            selectImage();
        }
    }

    public void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MaterialAlertDialog_Rounded);
        builder.setTitle("Choose Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    cameraIntent();
                } else if (items[item].equals("Gallery")) {
                    galleryIntent();
                }
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);
    }

    public void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d(TAG, "Crop Image Response: YES");
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                    setResult(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void setResult(Bitmap bitmap) {
        if (freeSizeImage) {
            Log.d(TAG, "check 1");
            if (bitmap.getWidth() > 1024 || bitmap.getHeight() > 1024) {
                bitmap = resize(bitmap, 1024, 1024);
                /*double width = bitmap.getWidth();
                double height = bitmap.getHeight();
                if(bitmap.getWidth()>bitmap.getHeight()) {
                    double d = height/width;
                    Log.d(TAG,"size 1:"+(int)(Math.round(1024*d))+" AND size2: "+1024*d);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1024, (int)(Math.round(1024*d)), false);
                }else{
                    double d = width/height;
                    Log.d(TAG,"size 1:"+(int)(Math.round(1024*d))+" AND size2: "+1024*d);
                    bitmap = Bitmap.createScaledBitmap(bitmap,(int) Math.round(1024*d), 1024, false);
                }*/
            }
        } else {
            if (bitmap.getWidth() > 960)
                bitmap = Bitmap.createScaledBitmap(bitmap, 960, 960, false);
        }
        Log.d(TAG, "check 2");
        imageString = bitmapToString(bitmap);

        Bitmap newBitmap = bitmap;
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
        byte[] byteArray1 = stream1.toByteArray();
        Log.d(TAG, "check 3 , Size : " + bitmap.getWidth() + " - " + bitmap.getHeight() + " - " + byteArray1.length);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteArray = stream.toByteArray();

        Log.d(TAG, "check 3-1 , Size : " + bitmap.getWidth() + " - " + bitmap.getHeight() + " - " + byteArray.length);

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "check 4");

        Intent intent = new Intent();
        intent.putExtra("image", byteArray);
        Log.d(TAG, "check 5");
        setResult(RESULT_OK, intent);
        Log.d(TAG, "check 6");
        finish();
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        if (freeSizeImage) {

            setResult(bitmap);
        } else {
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;
            try {
                if (destination.createNewFile()) {
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();

                    Uri uri = Uri.fromFile(destination);
                    CropImage.activity(uri)
                            .setAspectRatio(1, 1)
                            .start(this);
                } else {
                    Log.e(TAG, "File creation error");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                if (freeSizeImage) {
                    setResult(bitmap);
                } else {
                    imageString = bitmapToString(bitmap);
                    CropImage.ActivityBuilder activity = CropImage.activity(data.getData());
                    activity.setAspectRatio(1, 1).start(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
