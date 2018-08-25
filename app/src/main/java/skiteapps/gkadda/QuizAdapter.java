package skiteapps.gkadda;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

/**
 * Created by Abdelrahman Hesham on 10/26/2017.
 */

public class QuizAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int AD_TYPE = 1;
    private static final int CONTENT_TYPE = 2;

    private ArrayList<Quiz> quizzes;

    private QuizAdapter.OnItemClick onItemClick;

    public QuizAdapter(ArrayList<Quiz> quizzes, QuizAdapter.OnItemClick onItemClick) {
        this.quizzes = quizzes;
        this.onItemClick = onItemClick;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 5 == 0 && position != 0) {
            return AD_TYPE;
        }
        return CONTENT_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTENT_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item, null);
            QuizAdapter.ViewHolder holder = new QuizAdapter.ViewHolder(view);
            return holder;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad, null);
        QuizAdapter.AdViewHolder holder = new QuizAdapter.AdViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == CONTENT_TYPE) {
            QuizAdapter.ViewHolder holder1 = (QuizAdapter.ViewHolder)holder;
            holder1.bind(position);
        } else {
            QuizAdapter.AdViewHolder holder1 = (QuizAdapter.AdViewHolder) holder;
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            holder1.adView.loadAd(adRequest);
            if (position % 2 == 0) {
                holder1.cardView.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                holder1.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subject;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            cardView = itemView.findViewById(R.id.single_item);
            cardView.setOnClickListener(this);
        }

        public void bind(int position) {
            subject.setText(quizzes.get(position).getQuizName());
            if (position % 2 == 0) {
                cardView.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onItemClick.setOnItemClick(position);
        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        AdView adView;
        CardView cardView;

        public AdViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.single_item);
            adView = itemView.findViewById(R.id.adView);
            cardView.setVisibility(View.GONE);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    cardView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public interface OnItemClick {
        void setOnItemClick(int position);
    }

}