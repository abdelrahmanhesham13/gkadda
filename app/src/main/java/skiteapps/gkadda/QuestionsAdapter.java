package skiteapps.gkadda;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Abdelrahman Hesham on 10/27/2017.
 */

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private ArrayList<QuestionShortQA> questions;

    private QuestionsAdapter.OnItemClick onItemClick;

    public QuestionsAdapter(ArrayList<QuestionShortQA> questions, QuestionsAdapter.OnItemClick onItemClick) {
        this.questions = questions;
        this.onItemClick = onItemClick;
    }



    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item,null);
        QuestionsAdapter.ViewHolder holder=new QuestionsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(QuestionsAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView subject;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            cardView = itemView.findViewById(R.id.single_item);
            cardView.setOnClickListener(this);
        }
        public void bind(int position){
            subject.setText(questions.get(position).getQuestion());
            if (position % 2 == 0){
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

    public interface OnItemClick {
        void setOnItemClick(int position);
    }

}
