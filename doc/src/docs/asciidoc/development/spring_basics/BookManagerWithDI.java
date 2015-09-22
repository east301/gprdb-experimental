package com.example.bookmanager;

@Component
public class BookManagerWithDI {

    @Autowired
    private BookInfoRetriever retriever;

    public void registerBook(String isbn) {
        BookInfo bookInfo = this.retriever.retrieve(isbn);
        registerBookInfo(bookInfo);
    }

}

@Component
public class AmazonBookInfoRetriever implements BookInfoRetriever {

    @Autowired
    private String awsAccessKeyId;

    @Autowired
    private String awsAccessSecretKey;

    @Override
    public BookInfo retrieve(String idbn) {
        ...
    }

}

@Component
public class GoogleBookInfoRetriever implements BookInfoRetriever {

    @Override
    public BookInfo retrieve(String idbn) {
        ...
    }

}
