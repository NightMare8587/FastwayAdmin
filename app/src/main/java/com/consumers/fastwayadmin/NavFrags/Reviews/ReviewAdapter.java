package com.consumers.fastwayadmin.NavFrags.Reviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Holder> {
    List<String> customerID = new ArrayList<>();
    List<String> rating = new ArrayList<>();
    List<String> customerName = new ArrayList<>();
    List<String> customerReview = new ArrayList<>();
    Context context;

    public ReviewAdapter(List<String> customerID, List<String> rating, List<String> customerName, List<String> customerReview, Context context) {
        this.customerID = customerID;
        this.rating = rating;
        this.customerName = customerName;
        this.customerReview = customerReview;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.restaurant_review_card,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.name.setText(customerName.get(position));
        holder.review.setText(customerReview.get(position));
        holder.rating.setText(rating.get(position) + "/5");
        holder.cardView.setOnClickListener(click -> {

        });
    }

    @Override
    public int getItemCount() {
        return customerID.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView name,review,rating;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextViewReview);
            review = itemView.findViewById(R.id.reviewTextView);
            rating = itemView.findViewById(R.id.ratingReviewTextView);
            cardView = itemView.findViewById(R.id.ratingReviewCardView);
        }
    }
}
