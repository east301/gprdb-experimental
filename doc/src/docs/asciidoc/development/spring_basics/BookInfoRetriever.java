package com.example.bookmanager;

public interface BookInfoRetriever {

    BookInfo retrieve(String idbn);

}

public interface BookInfo {

    String getTitle();

    String[] getAuthors();

    String getPublisher();

    Date getPublishedAt();

}
