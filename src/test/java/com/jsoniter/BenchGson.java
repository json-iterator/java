package com.jsoniter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.jsoniter.extra.GsonCompatibilityMode;
import com.jsoniter.spi.DecodingMode;
import com.jsoniter.spi.JsoniterSpi;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

@State(Scope.Thread)
public class BenchGson {
    private GsonCompatibilityMode gsonCompatibilityMode;
    private Gson gson;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        gson = new GsonBuilder()
                .setDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
                .create();
        gsonCompatibilityMode = new GsonCompatibilityMode.Builder().setDateFormat("EEE MMM dd HH:mm:ss Z yyyy").build();
        JsoniterSpi.setCurrentConfig(gsonCompatibilityMode);
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        if (params != null) {
            if (params.getBenchmark().contains("jsoniterDynamicCodegenDecoder")) {
                JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
            }
        }
    }

    @Benchmark
    public void gsonDecoder(Blackhole bh) throws IOException {
        FileInputStream stream = new FileInputStream("/tmp/tweets.json");
        InputStreamReader reader = new InputStreamReader(stream);
        try {
            bh.consume(gson.fromJson(reader, new TypeReference<List<Tweet>>() {
            }.getType()));
        } finally {
            reader.close();
            stream.close();
        }
    }

    @Benchmark
    public void jsoniterReflectionDecoder(Blackhole bh) throws IOException {
        FileInputStream stream = new FileInputStream("/tmp/tweets.json");
        JsonIterator iter = JsonIteratorPool.borrowJsonIterator();
        try {
            iter.reset(stream);
            bh.consume(iter.read(new TypeReference<List<Tweet>>() {
            }.getType()));
        } finally {
            JsonIteratorPool.returnJsonIterator(iter);
            stream.close();
        }
    }

//
//    @Benchmark
//    public void jsoniterDynamicCodegenDecoder(Blackhole bh) throws IOException {
//        bh.consume(JsonIterator.deserialize(gsonCompatibilityMode, json, BagOfPrimitives.class));
//    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "BenchGson",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    public static class Tweet {
        @JsonProperty
        String coordinates;
        @JsonProperty
        boolean favorited;
        @JsonProperty
        Date created_at;
        @JsonProperty
        boolean truncated;
        @JsonProperty
        Tweet retweeted_status;
        @JsonProperty
        String id_str;
        @JsonProperty
        String in_reply_to_id_str;
        @JsonProperty
        String contributors;
        @JsonProperty
        String text;
        @JsonProperty
        long id;
        @JsonProperty
        String retweet_count;
        @JsonProperty
        String in_reply_to_status_id_str;
        @JsonProperty
        Object geo;
        @JsonProperty
        boolean retweeted;
        @JsonProperty
        String in_reply_to_user_id;
        @JsonProperty
        String in_reply_to_screen_name;
        @JsonProperty
        Object place;
        @JsonProperty
        User user;
        @JsonProperty
        String source;
        @JsonProperty
        String in_reply_to_user_id_str;
    }

    static class User {
        @JsonProperty
        String name;
        @JsonProperty
        String profile_sidebar_border_color;
        @JsonProperty
        boolean profile_background_tile;
        @JsonProperty
        String profile_sidebar_fill_color;
        @JsonProperty
        Date created_at;
        @JsonProperty
        String location;
        @JsonProperty
        String profile_image_url;
        @JsonProperty
        boolean follow_request_sent;
        @JsonProperty
        String profile_link_color;
        @JsonProperty
        boolean is_translator;
        @JsonProperty
        String id_str;
        @JsonProperty
        int favourites_count;
        @JsonProperty
        boolean contributors_enabled;
        @JsonProperty
        String url;
        @JsonProperty
        boolean default_profile;
        @JsonProperty
        long utc_offset;
        @JsonProperty
        long id;
        @JsonProperty
        boolean profile_use_background_image;
        @JsonProperty
        int listed_count;
        @JsonProperty
        String lang;
        @JsonProperty("protected")
        @SerializedName("protected")
        boolean isProtected;
        @JsonProperty
        int followers_count;
        @JsonProperty
        String profile_text_color;
        @JsonProperty
        String profile_background_color;
        @JsonProperty
        String time_zone;
        @JsonProperty
        String description;
        @JsonProperty
        boolean notifications;
        @JsonProperty
        boolean geo_enabled;
        @JsonProperty
        boolean verified;
        @JsonProperty
        String profile_background_image_url;
        @JsonProperty
        boolean defalut_profile_image;
        @JsonProperty
        int friends_count;
        @JsonProperty
        int statuses_count;
        @JsonProperty
        String screen_name;
        @JsonProperty
        boolean following;
        @JsonProperty
        boolean show_all_inline_media;
    }

    static class Feed {
        @JsonProperty
        String id;
        @JsonProperty
        String title;
        @JsonProperty
        String description;
        @JsonProperty("alternate")
        @SerializedName("alternate")
        List<Link> alternates;
        @JsonProperty
        long updated;
        @JsonProperty
        List<Item> items;

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder()
                    .append(id)
                    .append("\n").append(title)
                    .append("\n").append(description)
                    .append("\n").append(alternates)
                    .append("\n").append(updated);
            int i = 1;
            for (Item item : items) {
                result.append(i++).append(": ").append(item).append("\n\n");
            }
            return result.toString();
        }
    }

    static class Link {
        @JsonProperty
        String href;

        @Override
        public String toString() {
            return href;
        }
    }

    static class Item {
        @JsonProperty
        List<String> categories;
        @JsonProperty
        String title;
        @JsonProperty
        long published;
        @JsonProperty
        long updated;
        @JsonProperty("alternate")
        @SerializedName("alternate")
        List<Link> alternates;
        @JsonProperty
        Content content;
        @JsonProperty
        String author;
        @JsonProperty
        List<ReaderUser> likingUsers;

        @Override
        public String toString() {
            return title
                    + "\nauthor: " + author
                    + "\npublished: " + published
                    + "\nupdated: " + updated
                    + "\n" + content
                    + "\nliking users: " + likingUsers
                    + "\nalternates: " + alternates
                    + "\ncategories: " + categories;
        }
    }

    static class Content {
        @JsonProperty
        String content;

        @Override
        public String toString() {
            return content;
        }
    }

    static class ReaderUser {
        @JsonProperty
        String userId;

        @Override
        public String toString() {
            return userId;
        }
    }
}
