package com.surajvanshsv.quoteapps.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.model.Quote;

import java.util.ArrayList;
import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    // ✅ Always initialize
    private List<Quote> quoteList = new ArrayList<>();
    private final OnQuoteClickListener listener;

    public QuoteAdapter(OnQuoteClickListener listener) {
        this.listener = listener;
    }

    public void setQuotes(List<Quote> quotes) {
        // ✅ Never allow null to be set
        this.quoteList = quotes != null ? quotes : new ArrayList<>();
        notifyDataSetChanged();
    }

    public Quote getQuoteAt(int position) {
        return quoteList.get(position);
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quoteList.get(position);
        holder.tvBody.setText(quote.getBody());
        holder.tvAuthor.setText("— " + quote.getAuthor());

        if (listener != null) {
            holder.btnShare.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnShare.setOnClickListener(v -> listener.onShareClick(quote));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(quote));
        } else {
            holder.btnShare.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return quoteList != null ? quoteList.size() : 0;
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

    public interface OnQuoteClickListener {
        void onShareClick(Quote quote);
        void onDeleteClick(Quote quote);
    }
}
