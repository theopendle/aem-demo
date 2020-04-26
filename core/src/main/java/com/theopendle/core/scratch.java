package com.theopendle.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Scratch {
    public static void main(final String[] args) {
        final List<BlogPost> posts = Arrays.asList(
                new BlogPost("one", BlogPostType.NEWS, 5),
                new BlogPost("two", BlogPostType.NEWS, 2),
                new BlogPost("thre", BlogPostType.GUIDE, 5)
        );

        System.out.println(posts.stream().collect(Collectors.groupingBy(BlogPost::getLikes, Collectors.counting())));
    }

    @AllArgsConstructor
    @Getter
    static class BlogPost {
        String title;
        BlogPostType type;
        int likes;
    }

    @Getter
    enum BlogPostType {
        NEWS,
        REVIEW,
        GUIDE
    }
}