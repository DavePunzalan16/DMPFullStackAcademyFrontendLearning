package com.dmpacademy.common.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts YouTube video IDs from various URL formats.
 * Supported patterns:
 * - https://www.youtube.com/watch?v=VIDEO_ID
 * - https://youtu.be/VIDEO_ID
 * - https://www.youtube.com/embed/VIDEO_ID
 */
public final class YouTubeUrlParser {

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
            "(?:https?://)?(?:www\\.)?(?:" +
                    "youtube\\.com/watch\\?v=|" +
                    "youtu\\.be/|" +
                    "youtube\\.com/embed/" +
                    ")([a-zA-Z0-9_-]{11})"
    );

    private YouTubeUrlParser() {}

    /**
     * Extracts the video ID from a YouTube URL.
     *
     * @param url the YouTube URL
     * @return Optional containing the video ID, or empty if URL doesn't match
     */
    public static Optional<String> extractVideoId(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }
        Matcher matcher = YOUTUBE_PATTERN.matcher(url.trim());
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }

    /**
     * Validates whether a URL is a recognized YouTube URL.
     *
     * @param url the URL to validate
     * @return true if the URL matches a known YouTube pattern
     */
    public static boolean isValidYouTubeUrl(String url) {
        return extractVideoId(url).isPresent();
    }
}
