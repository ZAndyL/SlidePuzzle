package com.andyliang.pawwzle;

/**
 * Created by Zheng (Andy) Liang on 7/23/2015.
 */
public class RedditResponse {

    public RedditData data;

    static class RedditData{

        public RedditChild[] children;

        static class RedditChild{

            public RedditChildData data;

            static class RedditChildData{

                public String url;
                public RedditPreview preview;

                static class RedditPreview{
                    public RedditImage[] images;

                    static class RedditImage{
                        public RedditSourceImage source;

                        static class RedditSourceImage{
                            public String url;
                        }
                    }
                }
            }
        }
    }

}