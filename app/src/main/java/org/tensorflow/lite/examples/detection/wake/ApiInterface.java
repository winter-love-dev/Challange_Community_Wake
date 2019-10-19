package org.tensorflow.lite.examples.detection.wake;


import org.tensorflow.lite.examples.detection.wake.FragmentFeed.Item_Feed_List;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.ItemCertifyingList;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.Item_Project_List;
import org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Item_Complain_Certi_Shot_List;
import org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Item_Complain_List;
import org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Item_My_JoinList;
import org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Item_getExpRecordList;
import org.tensorflow.lite.examples.detection.wake.FragmentProof.Item_Proof_List;
import org.tensorflow.lite.examples.detection.wake.Produce.Item_Opinion;
import org.tensorflow.lite.examples.detection.wake.Produce.Item_Suggestion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface
{
//    @FormUrlEncoded
//    @POST("workSpace_upload.php")
//    Call<WorkUpModel> WorkUpdate(
//            // 서버로 보낼 값을 아래 변수에 담는다.
//            // 값이 담긴 변수를 아래 필드에 담는다.
//            // 필드 이름으로 POST 를 받는다.
//            @Field("IntentAddress1") String IntentAddress1,
//            @Field("IntentAddress2") String IntentAddress2,
//            @Field("IntentWorkSpaceName") String IntentWorkSpaceName,
//            @Field("IntentWorkSpaceContact") String IntentWorkSpaceContact,
//            @Field("IntentWorkSpaceTableMax") String IntentWorkSpaceTableMax,
//            @Field("IntentWorkSpacePd") String IntentWorkSpacePd,
//            @Field("IntentWorkSpacePw") String IntentWorkSpacePw,
//            @Field("IntentWorkSpacePm") String IntentWorkSpacePm,
//            @Field("getWorkSpaceIntroduce") String getWorkSpaceIntroduce,
//            @Field("imageFile") File imageFile, // 이미지 파일
//            @Field("HostId") String HostId
//    );

    // 프로그램 제안 글 불러오기
    @FormUrlEncoded
    @POST("getSuggestion.php")
    Call<List<Item_Suggestion>> getSuggestionList(@Field("id") String id);

    // 마이페이지에서 참여중인 프로젝트 불러오기
    @FormUrlEncoded
    @POST("getMyJoinProjectList.php")
    Call<List<Item_My_JoinList>> getMyJoinList(@Field("getId") String id);

    // 인증방법 제안 글 불러오기
    @FormUrlEncoded
    @POST("getOpinion.php")
    Call<List<Item_Opinion>> getOpinionList(@Field("id") String id);

    // 프로젝트 목록 불러오기
    @FormUrlEncoded
    @POST("getProjectList.php")
    Call<List<Item_Project_List>> getProjectList(@Field("id") String id);

    // 인증 목록 불러오기
    @FormUrlEncoded
    @POST("getCertiList.php")
    Call<List<Item_Proof_List>> getProofList(@Field("getId") String id);

    // 피드 목록 불러오기
    @FormUrlEncoded
    @POST("getWakeFeed.php")
    Call<List<Item_Feed_List>> getFeedList(@Field("getId") String id);

    // 챌린지 상세 페이지에서 유저들의 인증샷 목록 불러오기
    @FormUrlEncoded
    @POST("getWakeFeedThisProject.php")
    Call<List<ItemCertifyingList>> getUserSertifyingShot(@Field("getIndex") String index);

    // 신고목록 불러오기
    @FormUrlEncoded
    @POST("getComplainList.php")
    Call<List<Item_Complain_List>> getComplainList(@Field("getId") String index);

    // 신고목록 불러오기
    @FormUrlEncoded
    @POST("getComplainCertiShot.php")
    Call<List<Item_Complain_Certi_Shot_List>>
                            getComplainCertiList(@Field("GET_COMPLAIN_DISPOSE_USER") String GET_COMPLAIN_DISPOSE_USER,
                                                 @Field("GET_COMPLAIN_JOIN_INDEX") String GET_COMPLAIN_JOIN_INDEX,
                                                 @Field("GET_COMPLAIN_CONTENT") String GET_COMPLAIN_CONTENT,
                                                 @Field("GET_COMPLAIN_CERTI__INDEX") String GET_COMPLAIN_CERTI__INDEX);

    // Exp 획득 기록 불러오기
    @FormUrlEncoded
    @POST("getExpRecord.php")
    Call<List<Item_getExpRecordList>> getExpRecordList(@Field("getId") String index);

//    // 홈화면에 게시물 불러오기
//    @FormUrlEncoded
//    @POST("getSuggestion.php")
//    Call<List<Item_Suggestion>> WorkList_Home(@Field("id") String id);

    // 대화목록 불러오기
//    @FormUrlEncoded
//    @POST("Chat_Running_List.php")
//    Call<List<Chat_Item_Room_List>> ChatRoomList(@Field("user_index") String user_index);


}


