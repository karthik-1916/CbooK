package com.example.ebook.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.ebook.AsyncDownload;
import com.example.ebook.R;
import com.example.ebook.SingleBookData;
import com.example.ebook.activities.DataFetcherActivity;
import com.google.android.material.snackbar.Snackbar;

public class BookDetailsPageDialog extends DialogFragment {

    private static final String BOOK_TITLE = "book_title";
    private static final String AUTHOR = "author";
    private static final String PUBLISHER = "publisher";
    private static final String YEAR = "year";
    private static final String EXTENSION = "extension";
    private static final String ISBN = "isbn";
    private static final String SIZE = "size";
    private static final String COVER_LINK = "cover_link";
    private static final String DOWNLOAD_LINK = "download_link";

    private ImageView cover;
    private TextView title;
    private TextView author;
    private TextView size;
    private TextView publisher;
    private TextView year;
    private TextView isbn;
    private TextView extension;


    public BookDetailsPageDialog newInstance(SingleBookData bookData) {
        BookDetailsPageDialog bookDetailsPageDialog = new BookDetailsPageDialog();
        Bundle args = new Bundle();
        args.putString(BOOK_TITLE,bookData.getBookName());
        args.putString(AUTHOR,bookData.getAuthor());
        args.putString(PUBLISHER,bookData.getPublisher());
        args.putString(YEAR,bookData.getYear());
        args.putString(EXTENSION,bookData.getExtension());
        args.putString(ISBN,bookData.getIsbn());
        args.putString(SIZE,bookData.getSize());
        args.putString(COVER_LINK,bookData.getCoverLink());
        args.putString(DOWNLOAD_LINK, bookData.getBookDownloadLink());
        bookDetailsPageDialog.setArguments(args);
        return bookDetailsPageDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String title = args.getString(BOOK_TITLE);
        String author = args.getString(AUTHOR);
        String publisher = args.getString(PUBLISHER);
        String year = args.getString(YEAR);
        String extension = args.getString(EXTENSION);
        String isbn = args.getString(ISBN);
        String size = args.getString(SIZE);
        String coverLink = args.getString(COVER_LINK);
        String download_link = args.getString(DOWNLOAD_LINK);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.book_details_page_dialog, null);
        initLayout(view, coverLink, title, author, size, publisher, year, isbn, extension);
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(getActivity().findViewById(R.id.relative),"Added to download",Snackbar.LENGTH_SHORT).show();
                new AsyncDownload(title, extension, download_link, getContext()).execute();
            }
        });
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void initLayout(View view,String...args) {
        cover = view.findViewById(R.id.cover);
        title = view.findViewById(R.id.book_title);
        author = view.findViewById(R.id.author);
        size = view.findViewById(R.id.size);
        publisher = view.findViewById(R.id.publisher);
        year = view.findViewById(R.id.year);
        isbn = view.findViewById(R.id.isbn);
        extension = view.findViewById(R.id.extension);

        Glide.with(this).load(args[0]).into(cover);
        title.setText(args[1]);
        author.setText(args[2]);
        size.setText("Size:"+args[3]);
        publisher.setText(args[4]);
        year.setText(args[5]);
        isbn.setText(args[6]);
        extension.setText("Type:"+args[7]);
    }

}
