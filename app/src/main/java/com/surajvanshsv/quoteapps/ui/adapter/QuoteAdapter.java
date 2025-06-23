package com.surajvanshsv.quoteapps.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.surajvanshsv.quoteapps.R;
import com.surajvanshsv.quoteapps.model.Quote;

import java.util.ArrayList;
import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    private List<Quote> quoteList = new ArrayList<>();

    public void setQuotes(List<Quote> quotes) {
        this.quoteList = quotes;
        notifyDataSetChanged();
    }

    public Quote getQuoteAt(int position) {
        return quoteList.get(position);
    }

    public void removeQuoteAt(int position) {
        quoteList.remove(position);
        notifyItemRemoved(position);
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
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvBody, tvAuthor;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
        }
    }
}
