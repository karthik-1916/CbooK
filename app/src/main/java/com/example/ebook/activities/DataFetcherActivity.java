package com.example.ebook.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebook.BookData;
import com.example.ebook.dialogs.BookDetailsPageDialog;
import com.example.ebook.R;
import com.example.ebook.SingleBookData;
import com.example.ebook.adapters.BookDetailsAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class DataFetcherActivity extends AppCompatActivity {
    private static final String TAG = "__envy__";

    private int page = 2;
    private String nextPageLink = null;
    private boolean isLoading = false;

    private BookData bookData = new BookData();
    private SingleBookData singleBookData = new SingleBookData();

    private RecyclerView recyclerView;
    private BookDetailsAdapter adapter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_fetcher);
        /***Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
            initLayout();
        }***/
        performSearch(getIntent().getStringExtra("query"));
        initLayout();

    }

    private void initLayout() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.expanded_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BookDetailsAdapter(bookData, DataFetcherActivity.this, recyclerView);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new BookDetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "Inside onItemClick" + position);
                singleBookData.setExtension(bookData.getBookExtension(position));
                singleBookData.setSize(bookData.getBookSize(position));
                getBookDetails(bookData.getBookLink(position));

            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int getItemCount = layoutManager.getItemCount();
                //Log.d(TAG,"Total items "+getItemCount);
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                //Log.d(TAG,"Scrolling in progress and last seen item position is "+lastPosition);

                if (!isLoading && getItemCount <= lastPosition + 1
                        && (nextPageLink != null)) {
                    Log.d(TAG, "Loading page " + page);
                    Log.d(TAG, "Total books loaded " + getItemCount);
                    progressBar.setVisibility(View.VISIBLE);
                    isLoading = true;
                    performSearch(nextPageLink + page);
                    page = page + 1;
                }
            }
        });
    }

    private void performSearch(final String query) {
        final String searchQuery;
        searchQuery = "http://gen.lib.rus.ec/search.php?req=" + query;
        Thread sourceLibgen = new Thread(() -> {
            //get the Documents of searchQuery
            Document resultPage = getDoc(searchQuery);
            Elements elements = getElements(resultPage, "a[href]");
            if (nextPageLink == null) {
                Log.d(TAG, "Inside if block to to load nextPageLink");
                String link = "http://gen.lib.rus.ec/search.php?req=";
                nextPageLink = getNextPageLink(elements);
                if (nextPageLink != null)
                    nextPageLink = nextPageLink.substring(0, nextPageLink.length() - 1);
            }
            getBookNames(elements);
            getBookExtension(resultPage);
            getBookSizes(resultPage);
            getBookLinks(elements, "href");
            getBookPublishedYear(resultPage);
            //loadBookDetails(bookLinkList);

        });
        sourceLibgen.start();
    }

    public Document getDoc(String link) {
        Document doc = null;
        try {
            Log.d(TAG, "Connecting to " + link);
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            Log.e(TAG, "Failed to connect to " + link);
            e.printStackTrace();
        }
        return doc;
    }

    private Elements getElements(Document document, String element) {
        Elements elements = null;
        if (document != null) {
            Log.d(TAG, "Getting all the links");
            elements = document.select(element);
        }
        return elements;
    }

    private void getBookNames(Elements elements) {
        if (elements != null) {
            for (Element element : elements) {
                String href = element.attr("href");
                if (href.contains("book/index.php")) {
                    bookData.addBookName(element.text());
                }
            }
        }

    }

    private void getBookExtension(Document doc) {
        Elements extensions = null;
        if (doc != null) {
            extensions = doc.select("td");
        }
        if (extensions != null) {
            for (Element element : extensions) {
                String ext = element.text();
                if (ext.equals("pdf") || ext.equals("chm") || ext.equals("rar") || ext.equals("djvu")
                        || ext.equals("epub") || ext.equals("azw") || ext.equals("azw3") || ext.equals("zip")
                        || ext.equals("doc") || ext.equals("mobi") || ext.contains("txt")) {
                    bookData.addBookExtension(ext);

                }
            }
        }

    }

    private void getBookSizes(Document document) {
        Elements links = null;
        if (document != null) {
            links = document.getElementsByTag("td");
        }
        if (links != null) {
            for (Element link : links) {
                if (link.text().contains(" Mb") || link.text().contains(" Kb")) {
                    bookData.addBookSize(link.text());
                }
            }
        }

    }

    private void getBookPublishedYear(Document document) {
        Elements tdElements = null;
        if (document != null) {
            tdElements = document.select("td[nowrap]");

            if (tdElements != null) {
                for (Element element:tdElements) {
                    String yearText = element.text();

                    if (!yearText.contains("pdf") && !yearText.contains("chm") && !yearText.contains("rar") && !yearText.contains("djvu") && !yearText.contains("epub")
                    && !yearText.contains("awz") && !yearText.contains("awz3") && !yearText.contains("zip") && !yearText.contains("doc") && !yearText.contains("mobi")
                    && !yearText.contains("Kb") && !yearText.contains("Mb")) {
                        if (yearText.length() == 0) {
                            yearText = "Not Found";
                        }
                        Log.d(TAG, "Adding year "+yearText);
                        bookData.addBookPublishedYear(yearText);
                    }
                }
            }
        }
    }

    private void getBookLinks(Elements elements, String attr) {
        if (elements != null) {
            Log.d(TAG, "Getting Libgen link");
            for (Element element : elements) {
                String href = element.attr(attr);
                if (href.contains("libgen.lc/ads")) {
                    Log.d(TAG, "Adding " + href + " to bookLinks list");
                    bookData.addBookLinks(href);
                    runOnUiThread(() -> {
                        isLoading = false;
                        progressBar.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        }
    }

    private String getNextPageLink(Elements elements) {
        if (elements != null) {
            for (Element element : elements) {
                String href = element.attr("href");

                if (href.contains("sortmode=ASC&page=2")) {
                    Log.d(TAG, "Next page link " + href);
                    return href;
                }
            }
        }
        return null;
    }


    public void getBookDetails(String link) {
        Thread bookData = new Thread(() -> {
            Document document = getDoc(link);
            Elements elements = getElements(document, "td");

            if (elements != null) {

                for (Element element : elements) {

                    if (element.text().contains("Title:") && !element.text().contains("@book")) {
                        Log.d(TAG, element.text());
                        String text = element.text();

                        String bookName = text.substring(text.indexOf("Title"), text.indexOf("Author"));
                        singleBookData.setBookName(bookName);
                        Log.d(TAG, "title of the book is " + bookName);

                        Log.d(TAG, "******************************************");

                        String author = text.substring(text.indexOf("Author"), text.indexOf("Publisher"));
                        singleBookData.setAuthor(author);
                        Log.d(TAG, author);

                        Log.d(TAG, "******************************************");

                        String publisher = text.substring(text.indexOf("Publisher"), text.indexOf("Year"));
                        singleBookData.setPublisher(publisher);
                        Log.d(TAG, publisher);

                        Log.d(TAG, "******************************************");

                        String year = text.substring(text.indexOf("Year"), text.indexOf("ISBN"));
                        singleBookData.setYear(year);
                        Log.d(TAG, year);

                        Log.d(TAG, "******************************************");

                        String ISBN = text.substring(text.indexOf("ISBN"));
                        singleBookData.setIsbn(ISBN);
                        Log.d(TAG, ISBN);

                    }
                }
                Elements downloadLinkElements = document.select("a[href]");
                if (downloadLinkElements != null) {
                    Log.d(TAG, "getBookDetails: Getting Download Link");
                    for (Element downloadLinkElement: downloadLinkElements) {
                        String href = downloadLinkElement.attr("href");
                        if (href.contains("get.php")) {
                            Log.d(TAG, href);
                            singleBookData.setBookDownloadLink(href);
                        }
                    }
                }
            }

            Elements links = getElements(document, "img");
            if (links != null) {
                for (Element coverLink : links) {
                    singleBookData.setCoverLink(coverLink.absUrl("src"));
                    Log.d(TAG, coverLink.absUrl("src"));
                }
            }
            runOnUiThread(()->{
                progressBar.setVisibility(View.INVISIBLE);
                BookDetailsPageDialog bookDialog = new BookDetailsPageDialog().newInstance(singleBookData);
                //new AsyncDownload(bookData.getBookName(position),bookData.getBookExtension(position),bookData.getBookLink(position),getApplicationContext()).execute();

                FragmentTransaction transaction = ((FragmentActivity) DataFetcherActivity.this)
                        .getSupportFragmentManager()
                        .beginTransaction();
                bookDialog.show(transaction, "book_details");
            });
        });
        bookData.start();
    }


    /***private void loadBookDetails(List<String> bookLinks) {
     for (String bookLink : bookLinks) {
     Document document = getDoc(bookLink);
     Elements bookNameElement = getElements(document, "td");
     Elements downloadLinkElements = getElements(document, "a[href]");
     if ((bookNameElement != null) && (downloadLinkElements != null)) {
     for (Element element : bookNameElement) {
     String bookName = element.text();
     if (bookName.contains("Title: ") && !bookName.contains("Download via torrent")) {
     bookName = bookName.replace("Title: ", "");
     bookName = bookName.replace("Author",".");
     bookName = bookName.substring(0, bookName.indexOf(".") - 1);
     Log.d(TAG, bookName);
     bookNameList.add(bookName);
     runOnUiThread(() -> adapter.notifyDataSetChanged());
     }
     }
     for (Element downloadLink : downloadLinkElements) {
     String href = downloadLink.attr("href");
     if (href.contains("get.php")) {
     Log.d(TAG, href);
     downloadLinkList.add(href);
     }
     }
     }
     }
     }***/

}