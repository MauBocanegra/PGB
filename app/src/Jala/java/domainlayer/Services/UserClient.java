package domainlayer.Services;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by maubocanegra on 24/05/17.
 */

public interface UserClient {

    @Multipart
    //@POST("http://testsvcyonayarit.iog.digital/api/Message/SendImageMessages")
    @POST(WSConstant.WSLink+"Message/SendImageMessages")
    Call<ResponseBody> uploadPhoto(
            @Part("UserId") RequestBody UserId,
            @Part("DestinationId") RequestBody DestinationId,
            @Part("MessageTypeId") RequestBody MessageTypeId,
            @Part MultipartBody.Part photo
    );
}