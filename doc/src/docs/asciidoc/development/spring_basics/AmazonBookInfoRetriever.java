package com.example.bookmanager;

import lombok.RequiredArgsConstructor;

import java.utils.Map;

/**
 * @see https://aws.amazon.com/code/Product-Advertising-API/2478
 */
@RequiredArgsConstructor
public class AmazonBookInfoRetriever implements BookInfoRetriever {

    private final String awsAccessKeyId;
    private final String awsAccessSecretKey;

    @Override
    public BookInfo retrieve(String idbn) {
        Map<String, String> queryParameters = buildQueryParameters(isbn);
        String url = signAndBuildRequestUrl(
            queryParameters, this.awsAccessKeyId, this.awsAccessSecretKey);
        Object response = peformHttpRequest(url);
        return convertHttpResponseToBookInfo(response);
    }

}
