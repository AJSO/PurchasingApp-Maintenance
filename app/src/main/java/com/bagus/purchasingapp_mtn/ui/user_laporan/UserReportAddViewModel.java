package com.bagus.purchasingapp_mtn.ui.user_laporan;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.widget.ImageView;

import com.bagus.purchasingapp_mtn.base.BaseViewModel;
import com.bagus.purchasingapp_mtn.model.ApiResponse;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;

public class UserReportAddViewModel extends BaseViewModel<UserReportNavigator> {

    public UserReportAddViewModel(Context context) {
        super(context);
    }

    public void sendReport(JsonObject jsonObject) {
        getNavigator().showProgress();
        getServer().reportAdd(Utils.getStringPreference(getContext(), Constants.USER_TOKEN), jsonObject)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            getNavigator().onResult(true, response.body().message);
                        } else {
                            getNavigator().onResult(false, response.message());
                        }
                        getNavigator().hideProgress();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        getNavigator().onResult(false, t.getMessage());
                        getNavigator().hideProgress();
                    }
                });
    }

    public String previewCapturedImage(ImageView imageView, String uri) {
        String image = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            final Bitmap bitmap = BitmapFactory.decodeFile(Uri.parse(uri).getPath(), options);
            image = encodeImage(bitmap);
            imageView.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return image;
    }

    public String previewCapturedImage(ImageView imageView, Bitmap bitmap) {
        String image = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            image = encodeImage(bitmap);
            imageView.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return image;
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static class ViewModelFactory implements ViewModelProvider.Factory {
        private Context context;

        ViewModelFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserReportAddViewModel(context);
        }
    }
}
