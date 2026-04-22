package com.hccmac.communityforumbackend.utils;

/**
 * 热度计算工具类
 */
public class HotScoreUtil {

    private static final double VIEW_WEIGHT = 1D;
    private static final double COMMENT_WEIGHT = 5D;
    private static final double LIKE_WEIGHT = 3D;
    private static final double COLLECT_WEIGHT = 4D;

    public static long calculateHotScore(long viewCount, long commentCount, long likeCount, long collectCount) {
        double score = viewCount * VIEW_WEIGHT
                + commentCount * COMMENT_WEIGHT
                + likeCount * LIKE_WEIGHT
                + collectCount * COLLECT_WEIGHT;
        return Math.round(score);
    }
}
