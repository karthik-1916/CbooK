package com.example.ebook;

import java.util.ArrayList;
import java.util.List;

public class BookData {

    private List<String> bookNameList = new ArrayList<>();
    private List<String> bookExtensionList = new ArrayList<>();
    private List<String> bookSizeList = new ArrayList<>();
    private List<String> bookLinksList = new ArrayList<>();
    private List<String> bookPublishedYear = new ArrayList<>();

    public BookData(){

    }

    public void addBookName(String name) {
        bookNameList.add(name);
    }

    public void addBookExtension(String extension) {
        bookExtensionList.add(extension);
    }

    public void addBookSize(String size) {
        bookSizeList.add(size);
    }

    public void addBookLinks(String link) {
        bookLinksList.add(link);
    }

    public String getBookName(int position) {
        return bookNameList.get(position);
    }

    public String getBookExtension(int position) {
        return bookExtensionList.get(position);
    }

    public String getBookSize(int position) {
        return bookSizeList.get(position);
    }

    public String getBookLink(int position) {
        return bookLinksList.get(position);
    }

    public int getBookLength() {
        return bookLinksList.size();
    }

    public void addBookPublishedYear(String year) {
        bookPublishedYear.add(year);
    }

    public String getBookPublishedYear(int position) {
        return bookPublishedYear.get(position);
    }
}
