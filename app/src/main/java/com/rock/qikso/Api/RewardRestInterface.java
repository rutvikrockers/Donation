package com.rock.qikso.Api;

import com.rock.qikso.model.CommentsData;
import com.rock.qikso.model.GuestLogin;
import com.rock.qikso.model.MyCommentsData;
import com.rock.qikso.model.MyProjectsData;
import com.rock.qikso.pojo.ResponseForgotPassword;
import com.rock.qikso.pojo.ResponseFunderData;
import com.rock.qikso.pojo.ResponseProjectDetailData;
import com.rock.qikso.pojo.ResponseSignupPojo;
import com.rock.qikso.pojo.ResponseUpdateAccount;
import com.rock.qikso.pojo.ResponseUserDashboardData;
import com.rock.qikso.pojo.ResponseUserLogin;
import com.rock.qikso.pojo.ResponseUserProfile;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by rockers on 14/3/17.
 */

public interface RewardRestInterface {
    @GET("api/login/guest")
    Call<GuestLogin> loginUser(@Header("fscrwsf-token")String token );



    @FormUrlEncoded
    @POST("api/user/update_account")
    Call<ResponseUpdateAccount> UpdateAccount(
            @Header("www-token") String token,@Field("profile_data")String profiledata );



    @FormUrlEncoded
    @POST("api/projectobj/detail")
    Call<ResponseProjectDetailData> ProjectDetail(@Header("www-token") String token, @Field("project_id") String projectid);





    @FormUrlEncoded
    @POST("api/projectobj/comments")
    Call<CommentsData> ProjectCommentsDashboard(@Header("www-token") String token, @Field("project_id") String projectid, @Field("offset")String offset, @Field("view_type")String viewtype);




    @FormUrlEncoded
    @POST("api/projectobj/funders")
    Call<ResponseFunderData> Funders(@Header("www-token") String token, @Field("project_id") String projectid, @Field("offset")String offset);





    @FormUrlEncoded
    @POST("api/register")
    Call<ResponseSignupPojo> UserRegister(@Header("www-token") String Token, @Field("email") String Email, @Field("full_name") String Fullname, @Field("password") String Password);





    @FormUrlEncoded
    @POST("api/projectobj/addUpdateComment")
    Call<CommentsData> AddUpdateComments(@Header("www-token") String Token, @Field("project_id") String Projectid,@Field("comment_id") String Commmentid,@Field("comment_type") String CommmentType,@Field("comment") String Commment);






    @FormUrlEncoded
    @POST("api/login/user_login")
    Call<ResponseUserLogin> UserLogin(@Header("www-token") String Token, @Field("email") String Email, @Field("password") String Password);




    @POST("api/user/user_dashboard")
    Call<ResponseUserDashboardData>UserDashboard(@Header("www-token")String token );



/*    @POST("api/search/index")
    Call<ResponseSearchFilterCampaign> searchuser(@Header("www-token") String token, @Body FilteredCampains requestModel);*/

    @POST("api/user/mycomments")
    Call<MyCommentsData> MyComments(
            @Header("www-token") String token,@Query("user_id")String userid );


    @FormUrlEncoded
    @POST("api/user/profile")
    Call<ResponseUserProfile> UserProfile(
            @Header("www-token") String token,@Field("user_id")String userid );


    @POST("api/user/myprojects")
    Call<MyProjectsData> MyProjects(
            @Header("www-token") String token,@Query("user_id")String userid ,@Query("offset")String offset);


    @FormUrlEncoded
    @POST("api/login/forgetpassword")
    Call<ResponseForgotPassword> ForgotPassword(@Header("www-token") String Token, @Field("email") String Email);

}
