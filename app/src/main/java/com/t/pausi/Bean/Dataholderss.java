package com.t.pausi.Bean;


import com.t.pausi.Pojo.GetChatList;
import com.t.pausi.Pojo.GetChatResponse;

import java.util.List;

/**
 * Created by Nitin on 05/02/2018.
 */

public class Dataholderss {
    public static List<GetChatList> getChatLists;
    public static GetChatResponse getChatResponse;

    public static List<GetChatList> getGetChatLists() {
        return getChatLists;
    }

    public static void setGetChatLists(List<GetChatList> getChatLists) {
        Dataholderss.getChatLists = getChatLists;
    }

    public static GetChatResponse getGetChatResponse() {
        return getChatResponse;
    }

    public static void setGetChatResponse(GetChatResponse getChatResponse) {
        Dataholderss.getChatResponse = getChatResponse;
    }



}
