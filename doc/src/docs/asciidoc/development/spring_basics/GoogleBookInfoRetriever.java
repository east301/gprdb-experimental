package com.example.bookmanager;

import lombok.RequiredArgsConstructor;

import java.utils.Map;

/**
 * @see https://developers.google.com/books/docs/v1/using
 */
@RequiredArgsConstructor
public class GoogleBookInfoRetriever implements BookInfoRetriever {

    @Override
    public BookInfo retrieve(String idbn) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
        Object response = peformHttpRequest(url);
        return convertHttpResponseToBookInfo(response);
    }

}
