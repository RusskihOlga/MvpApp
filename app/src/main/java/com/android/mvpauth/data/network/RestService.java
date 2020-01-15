package com.android.mvpauth.data.network;

import com.android.mvpauth.data.network.res.AvatarUrlRes;
import com.android.mvpauth.data.network.res.CommentRes;
import com.android.mvpauth.data.network.res.LoginRes;
import com.android.mvpauth.data.network.res.ProductRes;
import com.android.mvpauth.data.storage.dto.LoginDto;
import com.android.mvpauth.utils.ConstantManager;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

public interface RestService {
    @GET("products")
    Observable<Response<List<ProductRes>>> getProductResObs (@Header(ConstantManager.IF_MODIFIED_HEADER) String lastEntityUpdate);

    @POST("products/{productId}/comments")
    Observable<CommentRes> sendComment(@Path("productId") String productId, @Body CommentRes commentRes);

    @Multipart
    @POST("avatar")
    Observable<AvatarUrlRes> uploadUserAvatar(@Part MultipartBody.Part file);

    @POST("login")
    Observable<LoginRes> authUserObs(@Body LoginDto loginDto);
}
