package com.rksdev.personallearningos.shared.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class TitleFetcherService {

    private static final int TIMEOUT_MS = 5000;

    public String fetchTitle(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .userAgent("Mozilla/5.0 (compatible; TitleFetcherBot/1.0)")
                    .followRedirects(true)
                    .get();

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
}