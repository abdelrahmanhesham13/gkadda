package skiteapps.gkadda;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

/**
 * Created by Abdelrahman Hesham on 10/23/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int AD_TYPE = 1;
    private static final int CONTENT_TYPE = 2;


    private ArrayList<Subject> subjects;

    private OnItemClick onItemClick;

    public RecyclerAdapter(ArrayList<Subject> subjects, OnItemClick onItemClick) {
        this.subjects = subjects;
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
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad, null);
        AdViewHolder holder = new AdViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == AD_TYPE) {
            AdViewHolder holder1 = (AdViewHolder) holder;
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            holder1.adView.loadAd(adRequest);
            if (position % 2 == 0) {
                holder1.cardView.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                holder1.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        } else {
            ViewHolder holder1 = (ViewHolder) holder;
            holder1.subject.setText(subjects.get(position).getSubjectName());
            if (position % 2 == 0) {
                holder1.cardView.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                holder1.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
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
