package skiteapps.gkadda;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Abdelrahman Hesham on 10/30/2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private ArrayList<Note> notes;

    private NotesAdapter.OnItemClick onItemClick;

    public NotesAdapter(ArrayList<Note> notes) {
        this.notes = notes;
    }



    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_single_item,null);
        NotesAdapter.ViewHolder holder=new NotesAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotesAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView head;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.head1);
            content = itemView.findViewById(R.id.head2);
            itemView.setOnClickListener(this);
        }
        public void bind(int position){
            head.setText(notes.get(position).getHead());
            content.setText(notes.get(position).getContent());
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