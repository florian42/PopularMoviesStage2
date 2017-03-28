package de.flo_aumeier.popularmoviesstage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import de.flo_aumeier.popularmoviesstage2.model.Review;

/**
 * Adapter holding a list of Reviews.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private ListItemClickListener mOnClickListener;

    private List<Review> mReviews;

    public ReviewAdapter(ListItemClickListener mOnClickListener, List<Review> mReviews) {
        this.mOnClickListener = mOnClickListener;
        this.mReviews = mReviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.list_item_review;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = layoutInflater.inflate(layoutIdForGridItem, parent,
                shouldAttachToParentImmediately);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Log.d(TAG, "Bound item #" + position);
        holder.bind(mReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }
    public interface ListItemClickListener {
        void onReviewAdapterListItemClick(int clickedItemIndex);
    }
    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView author;
        private ExpandableTextView review;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_review_author);
            review = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            itemView.setOnClickListener(this);
        }

        public void bind(Review review) {

            final String author = review.getAuthor();
            this.author.setText(author);
            final String reviewContent = review.getContent();
            this.review.setText(reviewContent);
            Log.d(TAG, "Binding: author: " + author + " content: " + reviewContent);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onReviewAdapterListItemClick(clickedPosition);
        }
    }
}
