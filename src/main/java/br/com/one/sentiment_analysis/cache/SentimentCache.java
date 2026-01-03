//package br.com.one.sentiment_analysis.cache;
//
//import java.util.HashMap;
//
//import br.com.one.sentiment_analysis.dto.request.SentimentAnalysisRequest;
//import br.com.one.sentiment_analysis.dto.response.SentimentResponse;
//
//public class SentimentCache {
//    private static final HashMap<String, SentimentResponse> CACHE = new HashMap<>();
//
//    public static SentimentResponse get(String key) {
//        return CACHE.get(key);
//    }
//
//    public static void put(String key, SentimentResponse value) {
//        CACHE.put(key, value);
//    }
//
//    public static boolean containsKey(String key) {
//        return CACHE.containsKey(key);
//    }
//
//    public static String cacheKeyValue(SentimentAnalysisRequest request) {
//        String key = Integer.toString(request.hashCode());
//        SentimentResponse response = get(key);
//        if (response == null) {
//            put(key, response);
//        }
//        return key;
//    }
//}
