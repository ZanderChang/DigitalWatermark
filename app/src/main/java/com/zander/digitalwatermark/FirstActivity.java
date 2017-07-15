package com.zander.digitalwatermark;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Zander on 7/11/2017.
 */

public class FirstActivity extends AppCompatActivity {
    private Button btn_take_photos;
    private Button btn_browse_album_for_adding;
    private Button btn_browse_album_for_detecting;
    private Uri imageUri;
    private static final int PHOTO_GRAPH = 1;
    private static final int CHOOSE_PHOTO = 2;
    private File outputImage;
    private Boolean isAdding = false;
    private Boolean SIMPLE_MODE = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FirstActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.INTERNET, Manifest.permission.CAMERA }, 1);
        } else {
            init();
        }
    }

    private void init() {
        btn_browse_album_for_adding = (Button) findViewById(R.id.btn_browse_album_for_adding);
        btn_browse_album_for_detecting = (Button) findViewById(R.id.btn_browse_album_for_detecting);
        btn_take_photos = (Button) findViewById(R.id.btn_take_photos);

        btn_take_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputImage = new File(getExternalCacheDir(), "temp.jpg");
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
                startActivityForResult(intent, PHOTO_GRAPH);
            }
        });

        btn_browse_album_for_adding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdding = true;
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_PHOTO);
            }
        });

        btn_browse_album_for_detecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdding = false;
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_PHOTO);
            }
        });

        RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                if (radioButtonId == R.id.radio_simple) {
                    SIMPLE_MODE = true;
                } else {
                    SIMPLE_MODE = false;
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, "没有相应权限", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_GRAPH:
                // 启动add watermark activity
                Intent intent = new Intent(FirstActivity.this, StartActivity.class);
                intent.putExtra("oriPic", outputImage.getAbsolutePath());
                intent.putExtra("SIMPLE_MODE", SIMPLE_MODE);
                startActivity(intent);
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleImageOnKitKat(data);
                }
                break;
            default:
                break;
        }
    }

    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        if (imagePath != null) {
            if (isAdding) {
                // 添加水印
                Intent newIntent = new Intent(FirstActivity.this, StartActivity.class);
                newIntent.putExtra("oriPic", imagePath);
                newIntent.putExtra("SIMPLE_MODE", SIMPLE_MODE);
                startActivity(newIntent);
            } else {
                // 检测水印
                Intent newIntent = new Intent(FirstActivity.this, MainActivity.class);
                newIntent.putExtra("oriPic", imagePath);
                newIntent.putExtra("from", "album");
                newIntent.putExtra("SIMPLE_MODE", SIMPLE_MODE);
                startActivity(newIntent);
            }
        } else {
            Toast.makeText(FirstActivity.this, "获取路径失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
