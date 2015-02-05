package com.coretronic.bdt;

/**
 * 測試response文字內容
 */
public class AppTestResponse {


    public static final String getDeatailResponseDeatil =  "{\n" +
            "\"status\":\"OK\",\n" +
            "\"news_id\":\"d123456\",\n" +
            "\"msgCode\":\"A01\",\n" +
            "\"time\":\"103/08/04 15:06\",\n" +
            "\"news_author\":\"張小瑜\",\n" +
            "\"news_photo\":\"https://s3.yimg.com/bt/api/res/1.2/57GavAZdLPJRq3wV5HGOUQ--/YXBwaWQ9eW5ld3M7cT04NTt3PTU1MA--/http://tw-tech.zenfs.com/7c9c18856b1579bce2956c59fe3fc6a9_1000.jpg\",\n" +
            "\"news_title\":\"比蘋果錶還炫！有訊息跑馬燈的智慧型戒指\",\n" +
            "\"news_type\":\"不分類\",\n" +
            "\"news_content\":\"蘋果日前才發表智慧型手錶Apple Watch，網路上對其外型與功能也有兩極化評價。如果你不喜歡老拿出大尺吋手機查看訊息、卻又對智慧型手錶敬而遠之，那Mota公司所推出的這款智慧型戒指，或許是另一個更佳的折衷選擇。由美國矽谷公司Mota所研發的Smartring，支援iOS與Android作業系統，可接收手機所收到的來電通知，以及臉書、推特或其他App訊息，並以文字滾動的方式顯示在OLED顯示螢幕上，使用者也可以調校文字滾動速度、聲音或震動通知等，在開會、看電影或通勤途中，可以更不著痕跡地來查看其內容。Smartring具備防水防塵能力，且長達３天的電池續航力與手機搭配更是游刃有餘。與動輒近萬元的穿戴式裝置相比，Mota smartring更主打經濟實惠的路線。消費者只需要付出150美元（約合台幣4,500元）便可以輕鬆擁有。預計2015年三月份上市。\",\n" +
            "\"favor_article\":\"1\"\n" +
            "}\n";

    public static final String favorResponse =  "{\n" +
            "\"status\":\"OK\",\n" +
            "\"msgCode\":\"A01\",\n" +
            "\"favor_article\":\"1\"\n" +
            "}";

    public static  final String healthArticleResponse = "{\n" +
            "    \"status\": \"OK\",\n" +
            "    \"msgCode\": \"A01\",\n" +
            "    \"result\": [\n" +
            "        {\n" +
            "            \"news_id\": \"d123451\",\n" +
            "            \"time\": \"103/09/14 15:06\",\n" +
            "            \"news_photo\": null,\n" +
            "            \"news_title\": \"日曬 菸酒 化妝 老化3大殺手\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"news_id\": \"d123452\",\n" +
            "            \"time\": \"103/09/14 15:06\",\n" +
            "            \"news_photo\": \"http://uc.udn.com.tw/magimages/19/PROJ_ARTICLE/486_1805/f_532660_1.jpg\",\n" +
            "            \"news_title\": \"22歲老菸槍胸悶 血管堵了2條\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"news_id\": \"d123453\",\n" +
            "            \"time\": \"103/09/14 15:06\",\n" +
            "            \"news_photo\": null,\n" +
            "            \"news_title\": \"吹冷氣頭痛 多披件衣服\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"news_id\": \"d123454\",\n" +
            "            \"time\": \"103/09/14 15:06\",\n" +
            "            \"news_photo\": null,\n" +
            "            \"news_title\": \"肝癌有家族性 要定期追蹤\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"news_id\": \"d123455\",\n" +
            "            \"time\": \"103/09/14 15:06\",\n" +
            "            \"news_photo\": \"http://uc.udn.com.tw/magimages/19/PROJ_ARTICLE/0_0/f_535423_1.jpg\",\n" +
            "            \"news_title\": \"名人養生／顏大和 心似笑彌勒 肚腩小一號\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"news_id\": \"d123456\",\n" +
            "            \"time\": \"103/09/15 15:06\",\n" +
            "            \"news_photo\": \"http://uc.udn.com.tw/NEWS/MEDIA/8936994-3547116.jpg?sn=141076238497213\",\n" +
            "            \"news_title\": \"3D列印眼眶骨 車禍男眼球歸位\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";


    public static  final String healthArticleResponse2 = "{\n" +
            "    \"status\": \"OK\",\n" +
            "    \"msgCode\": \"A01\",\n" +
            "    \"result\": [\n" +
            "        {\n" +
            "            \"news_id\": \"d123446\",\n" +
            "            \"time\": \"103/08/04 15:06\",\n" +
            "            \"news_photo\": null,\n" +
            "            \"news_title\": \"換心臟瓣膜 還翁彩色人生\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "         {\n" +
            "            \"news_id\": \"d123445\",\n" +
            "            \"time\": \"103/08/04 15:06\",\n" +
            "            \"news_photo\": null,\n" +
            "            \"news_title\": \"光線刺激 有助腦中風復健\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"news_id\": \"d123445\",\n" +
            "            \"time\": \"103/08/04 15:06\",\n" +
            "            \"news_photo\": \"http://uc.udn.com.tw/magimages/19/PROJ_ARTICLE/486_1805/f_530726_1.jpg\",\n" +
            "            \"news_title\": \"久咳、胸悶像石壓 竟心臟衰竭\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"news_id\": \"d123445\",\n" +
            "            \"time\": \"103/08/04 15:06\",\n" +
            "            \"news_photo\": \"http://uc.udn.com.tw/magimages/19/PROJ_ARTICLE/486_1394/f_532653_1.jpg\",\n" +
            "            \"news_title\": \"「浮腳筋」又復發 超音波揪病因\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"news_id\": \"d123445\",\n" +
            "            \"time\": \"103/08/04 15:06\",\n" +
            "            \"news_photo\": \"http://top1health.blob.core.windows.net/cdn/am/19709/62287.jpg\",\n" +
            "            \"news_title\": \"年輕人心悸胸痛 小心二尖瓣膜脫垂\",\n" +
            "            \"news_type\": \"不分類\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    public static  final String healthArticleResponse3 = "{\n" +
            "    \"status\": \"OK\",\n" +
            "    \"msgCode\": \"A01\",\n" +
            "    \"result\": [\n" +
            "    ]\n" +
            "}";
}
