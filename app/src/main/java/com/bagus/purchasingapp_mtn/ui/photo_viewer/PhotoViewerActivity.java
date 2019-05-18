package com.bagus.purchasingapp_mtn.ui.photo_viewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.api.AppServerConfig;
import com.bumptech.glide.Glide;

public class PhotoViewerActivity extends AppCompatActivity {

    ImageView imageView;
    ImageView close;
    String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Intent intent = getIntent();
        photo = intent.getStringExtra("photo");

        imageView = findViewById(R.id.photo);
        close = findViewById(R.id.close);

        if (intent == null) {
            imageView.setFocusable(false);
            imageView.setClickable(false);
        } else {
            Glide.with(PhotoViewerActivity.this).load(AppServerConfig.IMAGE_URL + photo).into(imageView);
        }

        close.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

}
