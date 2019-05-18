package com.bagus.purchasingapp_mtn.ui.user_laporan;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.bagus.purchasingapp_mtn.BR;
import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.base.BaseActivity;
import com.bagus.purchasingapp_mtn.databinding.UserActivityReportAddBinding;
import com.bagus.purchasingapp_mtn.utils.Camera.CameraActivity;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class UserReportAddActivity extends BaseActivity<UserActivityReportAddBinding, UserReportAddViewModel> implements UserReportNavigator {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 210;
    private static final int GALERY_CHOOSER_IMAGE_REQUEST_CODE = 220;
    UserActivityReportAddBinding binding;
    UserReportAddViewModel viewModel;

    String fileUri = null;
    String imageBase64 = null;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.user_activity_report_add;
    }

    @Override
    public UserReportAddViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new UserReportAddViewModel.ViewModelFactory(this)).get(UserReportAddViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.setNavigator(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.addFoto.setOnClickListener(view -> {
            final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Photo");
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals("Camera")) {
                    takePhoto();
                } else if (items[item].equals("Gallery")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });


        binding.btnSave.setOnClickListener(v -> {
            String judul = binding.etJudul.getText().toString().trim();
            String keterangan = binding.etKeterangan.getText().toString().trim();
            String lokasi = binding.etLokasi.getText().toString().trim();

            if (judul.isEmpty()) {
                binding.etJudul.setError("Mohon isi judul!");
                binding.etJudul.requestFocus();
            } else if (keterangan.isEmpty()) {
                binding.etKeterangan.setError("Mohon isi keterangan!");
                binding.etKeterangan.requestFocus();
            } else if (lokasi.isEmpty()) {
                binding.etLokasi.setError("Mohon isi lokasi1");
                binding.etLokasi.requestFocus();
            } else if (imageBase64 == null) {
                Toast.makeText(this, "Mohon isi foto!", Toast.LENGTH_SHORT).show();
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("reportTitle", judul);
                jsonObject.addProperty("reportDescription", keterangan);
                jsonObject.addProperty("reportLocation", lokasi);
                jsonObject.addProperty("reportByUserSID", Utils.getStringPreference(this, Constants.USER_SID));
                jsonObject.addProperty("reportPhoto", imageBase64);

                viewModel.sendReport(jsonObject);
            }

        });
    }


    private void takePhoto() {
        try {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALERY_CHOOSER_IMAGE_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.w("Test", data.toString());
                    fileUri = Uri.fromFile(new File(data.getStringExtra("responseData"))).toString();
                    imageBase64 = viewModel.previewCapturedImage(binding.photo, fileUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                File f = null;
                try {
                    f = new File(new URI(fileUri));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if (f.length() == 0) {
                    fileUri = null;
                }
                Toast.makeText(this.getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALERY_CHOOSER_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri selectedImageUri = data.getData();
                    Log.w("URI", selectedImageUri.getPath());
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    imageBase64 = viewModel.previewCapturedImage(binding.photo, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this.getApplicationContext(), "User cancelled getting picture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "Sorry! Failed to getting picture", Toast.LENGTH_SHORT).show();
            }
        }

        Log.w("Image", imageBase64);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    public void showProgress() {
        showProgressDialog("Saving...");
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();
    }

    @Override
    public void onResult(boolean status, String response) {
        if (status) {
            finish();
        } else {
            showAlertDialog("Failure", "Terjadi kesalahan : " + response);
        }
    }
}
