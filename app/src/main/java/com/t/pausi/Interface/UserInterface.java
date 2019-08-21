package com.t.pausi.Interface;


import com.t.pausi.Bean.AddressToLatLonResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * Created by nitin on 25-09-2017.
 */

public interface UserInterface {

//------------------------------------------social_login ------------------------------------------

    @POST("webservice/social_login")
    Call<ResponseBody> social_login(@Query("first_name") String first_name,
                                    @Query("last_name") String last_name,
                                    @Query("mobile") String mobile,
                                    @Query("email") String email,
                                    @Query("register_id") String register_id,
                                    @Query("social_id") String social_id,
                                    @Query("device_type") String device_type,
                                    @Query("user_type") String user_type,
                                    @Query("broker") String broker,
                                    @Query("lat") String lat,
                                    @Query("lon") String lon);


    //---------------------------------------login-----------------------------------------------------
    @POST("webservice/login")
    Call<ResponseBody> login(
            @Query("email") String email,
            @Query("password") String password,
            @Query("register_id") String register_id,
            @Query("device_type") String device_type,
            @Query("lat") String lat,
            @Query("lon") String lon
    );

    //---------------------------------------signup---------------------------------------------------------
    @Multipart
    @POST("webservice/signup")
    Call<ResponseBody> signup(@Query("first_name") String first_name,
                              @Query("last_name") String last_name,
                              @Query("email") String email,
                              @Query("mobile") String mobile,
                              @Query("country_code") String country_code,
                              @Query("password") String password,
                              @Query("broker") String broker,
                              @Query("service_id") String service_id,
                              @Query("user_type") String user_type,
                              @Query("register_id") String register_id,
                              @Query("lat") String lat,
                              @Query("lon") String lon,
                              @Query("device_type") String device_type,
                              @Part MultipartBody.Part file,
                              @Part MultipartBody.Part file1);


//----------------------------------------forgot_password---------------------------------------------------

    @POST("webservice/forgot_password")
    Call<ResponseBody> forgot_password(
            @Query("email") String email);

//----------------------------------------------------get_profile----------------------------------------------

    @POST("webservice/get_profile")
    Call<ResponseBody> get_profile(
            @Query("user_id") String user_id);

    //-----------------------------------------------update_user_image--------------------------------------------------
    //http://technorizen.com/WORKSPACE1/pausi/webservice/update_user_image
    @Multipart
    @POST("webservice/update_user_image")
    Call<ResponseBody> update_user_image(
            @Query("user_id") String user_id,
            @Part MultipartBody.Part file);

//--------------------------------------------user_update-------------------------------------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/user_update?user_id=2&first_name=Kap&last_name=Stha&mobile=43524352
    @Multipart
    @POST("webservice/user_update")
    Call<ResponseBody> user_update(@Query("user_id") String user_id,
                                   @Query("first_name") String first_name,
                                   @Query("last_name") String last_name,
                                   @Query("country_code") String country_code,
                                   @Query("user_type") String user_type,
                                   @Query("mobile") String mobile,
                                   @Query("broker") String broker,
                                   @Query("broker_title") String broker_title,
                                   @Query("offer") String offer,
                                   @Query("close_deals") String close_deals,
                                   @Query("agent_since") String agent_since,
                                   @Part MultipartBody.Part file,
                                   @Part MultipartBody.Part file1);


//------------------------------------------change_password---------------------------------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/change_password?user_id=2&old_password=98433&password=123456
    @POST("webservice/change_password")
    Call<ResponseBody> change_password(@Query("user_id") String user_id,
                                       @Query("old_password") String old_password,
                                       @Query("password") String password);

    @GET("maps/api/geocode/json")
    Call<AddressToLatLonResponse> getLatLonFromAddress(@Query("address") String address,
                                                       @Query("key") String key);

    //-------------------------------------- get wallpaper list -------------------------------------
    //http://technorizen.com/WORKSPACE1/pausi/webservice/all_wallpaper_list
    @POST("webservice/all_wallpaper_list")
    Call<ResponseBody> all_wallpaper_list();

    //------------------------------------- add wallpaper ---------------------------
    //http://technorizen.com/WORKSPACE1/pausi/webservice/set_wallpaper?user_id=2&wallpaper_id=2
    @POST("webservice/set_wallpaper")
    Call<ResponseBody> set_wallpaper(@Query("user_id") String user_id,
                                     @Query("wallpaper_id") String wallpaper_id);

    //---------------------------------- get fav property list ---------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/get_favourite_property_lists?user_id=2
    @POST("webservice/get_favourite_property_lists")
    Call<ResponseBody> get_favourite_property_lists(@Query("user_id") String user_id);

    //----------------------------------- get mls call ----------------------------

    @POST("webservice/all_services")
    Call<ResponseBody> all_services();

    //----------------------------------- update zip code --------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/update_zipcode?user_id=1&zipcode=45253
    @POST("webservice/update_zipcode")
    Call<ResponseBody> update_zipcode(@Query("user_id") String user_id,
                                      @Query("zipcode") String zipcode);

    //---------------------------------- get news list -------------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/news_lists?user_id=24
    @POST("webservice/news_lists")
    Call<ResponseBody> news_lists(@Query("user_id") String user_id);

    //---------------------------------- get conversation -------------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/get_conversation?get_conversation=24
    @POST("webservice/get_conversation")
    Call<ResponseBody> get_conversation(@Query("receiver_id") String receiver_id);

    //-------------------------------- all property list -----------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/all_listing_list?user_id=16
    @POST("webservice/all_listing_list")
    Call<ResponseBody> all_listing_list(@Query("user_id") String user_id,
                                        @Query("type") String type);

    //-------------------------------- insert chat ----------------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/insert_chat?sender_id=15&receiver_id=25&chat_message=TM
    @Multipart
    @POST("webservice/insert_chat")
    Call<ResponseBody> insert_chat(@Query("sender_id") String sender_id,
                                   @Query("receiver_id") String receiver_id,
                                   @Query("chat_message") String chat_message,
                                   @Part MultipartBody.Part file);

    //http://technorizen.com/WORKSPACE1/pausi/webservice/get_chat?

    @POST("webservice/get_chat")
    Call<ResponseBody> get_chat(@Query("sender_id") String sender_id,
                                @Query("receiver_id") String receiver_id);

    //-------------------------------- add to favorite  ----------------------------------

    //http://technorizen.com/WORKSPACE1/pausi/webservice/add_to_favourite?sender_id=15&receiver_id=25&chat_message=TM
    @POST("webservice/add_to_favourite")
    Call<ResponseBody> add_to_favourite(@Query("user_id") String user_id,
                                        @Query("property_id") String property_id);

    //---------------------------------------filter ---------------------------------------------------------
    //http://technorizen.com/WORKSPACE1/pausi/webservice/all_listing_list?type=FILTER

    @POST("webservice/all_listing_list")
    Call<ResponseBody> all_listing_list2(@Query("user_id") String user_id,
                                         @Query("type") String type,
                                         @Query("property_type") String property_type,
                                         @Query("sale_type") String sale_type,
                                         @Query("from_price") String from_price,
                                         @Query("to_price") String to_price,
                                         @Query("from_bed") String from_bed,
                                         @Query("to_bed") String to_bed,
                                         @Query("from_bath") String from_bath,
                                         @Query("to_bath") String to_bath,
                                         @Query("sort_type") String sort_type,
                                         @Query("date_type") String date_type,
                                         @Query("status") String status,
                                         @Query("property_amenities") String property_amenities);


    //listing_details
    @POST("webservice/listing_details")
    Call<ResponseBody> listing_details(@Query("property_id") String property_id,
                                       @Query("user_id") String user_id);

    @Multipart
    @POST("webservice/add_new_property")
    Call<ResponseBody> add_new_property(@Query("user_id") String user_id,
                                        @Query("property_name") String property_name,
                                        @Query("property_price") String property_price,
                                        @Query("modified_price") String modified_price,
                                        @Query("property_type") String property_type,
                                        @Query("status") String status,
                                        @Query("sq_feet") String sq_feet,
                                        @Query("beds") String beds,
                                        @Query("baths") String baths,
                                        @Query("build_year") String build_year,
                                        @Query("acre_area") String acre_area,
                                        @Query("country") String country,
                                        @Query("state") String state,
                                        @Query("city") String city,
                                        @Query("address") String address,
                                        @Query("lat") String lat,
                                        @Query("lon") String lon,
                                        @Query("zipcode") String zipcode,
                                        @Query("sale_type") String sale_type,
                                        @Query("property_description") String property_description,
                                        @Query("address_2") String address_2,
                                        @Query("image_cnt") String image_cnt,
                                        @Query("property_amenities") String property_amenities,
                                        @Query("language") String language,
                                        @Part List<MultipartBody.Part> property_images);

    @POST("webservice/my_listing_list")
    Call<ResponseBody> my_listing_list(@Query("agent_id") String agent_id);

    @POST("webservice/update_property_status")
    Call<ResponseBody> update_property_status(
            @Query("user_id") String user_id,
            @Query("property_id") String property_id,
            @Query("news_type") String news_type,
            @Query("assign_user_id") String assign_user_id,
            @Query("sold_price") String sold_price,
            @Query("status") String status);


    @POST("webservice/delete_property_image")
    Call<ResponseBody> delete_property_image(@Query("property_image_id") String property_image_id);

    @Multipart
    @POST("webservice/update_property")
    Call<ResponseBody> update_property(@Query("user_id") String user_id,
                                       @Query("property_id") String property_id,
                                       @Query("property_name") String property_name,
                                       @Query("property_price") String property_price,
                                       @Query("modified_price") String modified_price,
                                       @Query("property_type") String property_type,
                                       @Query("status") String status,
                                       @Query("sq_feet") String sq_feet,
                                       @Query("beds") String beds,
                                       @Query("baths") String baths,
                                       @Query("build_year") String build_year,
                                       @Query("acre_area") String acre_area,
                                       @Query("country") String country,
                                       @Query("state") String state,
                                       @Query("city") String city,
                                       @Query("address") String address,
                                       @Query("lat") String lat,
                                       @Query("lon") String lon,
                                       @Query("zipcode") String zipcode,
                                       @Query("sale_type") String sale_type,
                                       @Query("property_description") String property_description,
                                       @Query("address_2") String address_2,
                                       @Query("image_cnt") String image_cnt,
                                       @Query("property_amenities") String property_amenities,
                                       @Query("language") String language,
                                       @Part List<MultipartBody.Part> property_images);


    //http://technorizen.com/WORKSPACE1/pausi/webservice/clear_conversation?sender_id=12&receiver_id=13
    @POST("webservice/clear_conversation")
    Call<ResponseBody> clear_conversation(@Query("sender_id") String sender_id,
                                          @Query("receiver_id") String receiver_id);

    //http://technorizen.com/WORKSPACE1/pausi/webservice/update_online_offline_status?user_id=12&status=Offline&timezone=Asia/Kolkata&last_seen=28/01/2019%2012:34:23

    @POST("webservice/update_online_offline_status")
    Call<ResponseBody> update_online_offline_status(@Query("user_id") String property_id,
                                                    @Query("status") String status,
                                                    @Query("timezone") String timezone,
                                                    @Query("last_seen") String last_seen);

    //add_remove_notify
    @POST("webservice/add_remove_notify")
    Call<ResponseBody> add_remove_notify(@Query("property_id") String property_id,
                                         @Query("user_id") String user_id);

    //get_property_notification
    @POST("webservice/get_property_notification")
    Call<ResponseBody> get_property_notification(@Query("r_user_id") String r_user_id);

    //http://35.180.157.237/pausi/webservice/get_all_user_list

    @POST("webservice/get_all_user_list")
    Call<ResponseBody> get_all_user_list();


}
