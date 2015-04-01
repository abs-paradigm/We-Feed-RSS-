package com.abs_paradigm.wefeedrss.FeedIO;

/**
 * Created by Dom on 2015-03-28.
 */

    public class Feed {

        private final String TAG = getClass().getSimpleName();

        private String title;
        private String description;

        public Feed(){

        }

        public Feed(String title, String description){
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

