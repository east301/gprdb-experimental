package com.example.bookmanager;

public class BookManager {

    public void registerBook(String isbn) {
        BookInfoRetriever retriever = new AmazonBookInfoRetriever(
            "access-key", "secret-key");

        // or
        //BookInfoRetriever retriever = new GoogleBookInfoRetriever();
        //    <-- choice of implementation is fixed at compile time

        BookInfo bookInfo = retriever.retrieve(isbn);
        registerBookInfo(bookInfo);
    }

}
