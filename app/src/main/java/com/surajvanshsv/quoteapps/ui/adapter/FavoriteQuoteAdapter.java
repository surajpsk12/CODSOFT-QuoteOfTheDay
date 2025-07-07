package com.surajvanshsv.quoteapps.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.model.FavoriteQuote;

import java.util.ArrayList;
import java.util.List;

public class FavoriteQuoteAdapter extends RecyclerView.Adapter<FavoriteQuoteAdapter.QuoteViewHolder> {

    private final List<FavoriteQuote> quoteList = new ArrayList<>();
    private final OnFavoriteQuoteClickListener listener;

    public interface OnFavoriteQuoteClickListener {
        void onShareClick(FavoriteQuote quote);
        void onDeleteClick(FavoriteQuote quote);
    }

    public FavoriteQuoteAdapter(OnFavoriteQuoteClickListener listener) {
        this.listener = listener;
    }

    public void setQuotes(List<FavoriteQuote> quotes) {
        quoteList.clear();
        quoteList.addAll(quotes);
        notifyDataSetChanged();
    }

    public FavoriteQuote getQuoteAt(int position) {
        return quoteList.get(position);
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        FavoriteQuote quote = quoteList.get(position);
        holder.tvBody.setText(quote.getBody());
        holder.tvAuthor.setText("— " + quote.getAuthor());

        holder.btnShare.setOnClickListener(v -> listener.onShareClick(quote));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(quote));
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvBody, tvAuthor;
        ImageButton btnShare, btnDelete;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
