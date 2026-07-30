package com.rksdev.personallearningos.shared.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TitleFetcherService {

    private static final int TIMEOUT_MS = 5000;
    private static final String BROWSER_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    private final ObjectMapper objectMapper;

    // Provider Registry: Maps domain keywords to their respective oEmbed API URL templates
    private static final Map<String, String> OEMBED_PROVIDERS = Map.of(
            "youtube.com", "https://www.youtube.com/oembed?url=%s&format=json",
            "youtu.be", "https://www.youtube.com/oembed?url=%s&format=json",
            "vimeo.com", "https://vimeo.com/api/oembed.json?url=%s",
            "spotify.com", "https://open.spotify.com/oembed?url=%s",
            "reddit.com", "https://www.reddit.com/oembed?url=%s",
            "tiktok.com", "https://www.tiktok.com/oembed?url=%s",
            "soundcloud.com", "https://soundcloud.com/oembed?url=%s&format=json",
            "codepen.io", "https://codepen.io/api/oembed?url=%s&format=json",
            "twitter.com", "https://publish.twitter.com/oembed?url=%s",
            "x.com", "https://publish.twitter.com/oembed?url=%s"
    );

    public String fetchTitle(String url) {
        // 1. Check if the URL belongs to a known oEmbed provider
        String oembedTitle = fetchTitleViaOEmbed(url);
        if (oembedTitle != null && !oembedTitle.isBlank()) {
            return oembedTitle.trim();
        }

        // 2. Generic HTML Jsoup fetching for all other standard websites
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .userAgent(BROWSER_USER_AGENT)
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .followRedirects(true)
                    .get();

            // Check OpenGraph/Twitter meta tags first (handles Medium, Dev.to, GitHub, etc.)
            String ogTitle = getMetaTagContent(doc);
            if (ogTitle != null && !ogTitle.isBlank()) {
                return ogTitle.trim();
            }

            String title = doc.title();
            return title.isBlank() ? null : title.trim();

        } catch (org.jsoup.HttpStatusException e) {
            throw new RuntimeException("URL returned HTTP error: " + e.getStatusCode(), e);
        } catch (java.net.SocketTimeoutException e) {
            throw new RuntimeException("Request timed out while fetching: " + url, e);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to fetch URL: " + url + " — " + e.getMessage(), e);
        }
    }

    /**
     * Resolves the oEmbed endpoint for matching domains and fetches the title JSON node.
     */
    private String fetchTitleViaOEmbed(String url) {
        try {
            String domainKey = findMatchingProvider(url);
            if (domainKey == null) return null;

            String endpointTemplate = OEMBED_PROVIDERS.get(domainKey);
            String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
            String oembedApiUrl = String.format(endpointTemplate, encodedUrl);

            String jsonResponse = Jsoup.connect(oembedApiUrl)
                    .ignoreContentType(true)
                    .timeout(TIMEOUT_MS)
                    .userAgent(BROWSER_USER_AGENT)
                    .execute()
                    .body();

            JsonNode node = objectMapper.readTree(jsonResponse);

            // Note: Twitter/X oEmbed returns "author_name" or HTML snippets, while others return "title"
            if (node.has("title")) {
                return node.get("title").asString();
            } else if (node.has("author_name")) {
                return "Post by " + node.get("author_name").asString();
            }
        } catch (Exception e) {
            // Silently fail over to standard Jsoup scraping
        }
        return null;
    }

    private String findMatchingProvider(String url) {
        try {
            String host = URI.create(url).getHost().toLowerCase();
            for (String domain : OEMBED_PROVIDERS.keySet()) {
                if (host.contains(domain)) {
                    return domain;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String getMetaTagContent(Document doc) {
        for (String selector : new String[]{"meta[property=og:title]", "meta[name=twitter:title]"}) {
            Element element = doc.selectFirst(selector);
            if (element != null && element.hasAttr("content")) {
                return element.attr("content");
            }
        }
        return null;
    }
}