package com.example.ebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebook.BookData;
import com.example.ebook.R;


public class BookDetailsAdapter extends RecyclerView.Adapter<BookDetailsAdapter.MyViewHolder> {

    public int totalItemCount;
    public int lastVisibleItem;
    public boolean isLoading;
    public int visibleThreshold = 5;

    private BookData bookData;
    private OnItemClickListener mListener;
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public BookDetailsAdapter(BookData bookData,Context context,RecyclerView recyclerView){
        this.bookData = bookData;
        mContext = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bookName.setText(bookData.getBookName(position));
        holder.bookExt.setText(bookData.getBookExtension(position));
        holder.bookSize.setText(bookData.getBookSize(position));
        holder.bookPublishedYear.setText(bookData.getBookPublishedYear(position));

    }

    @Override
    public int getItemCount() {
        return bookData.getBookLength();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView bookName;
        private TextView bookExt;
        private CardView cardView;
        private TextView bookSize;
        private TextView bookPublishedYear;

        public MyViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            bookName = (TextView)itemView.findViewById(R.id.book_name);
            bookExt = (TextView)itemView.findViewById(R.id.book_ext);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            bookSize = (TextView)itemView.findViewById(R.id.bookSize);
            bookPublishedYear = (TextView)itemView.findViewById(R.id.published_year);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
