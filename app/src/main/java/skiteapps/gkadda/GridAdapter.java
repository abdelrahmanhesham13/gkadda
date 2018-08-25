package skiteapps.gkadda;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hp world on 10/18/2017.
 */

public class GridAdapter extends BaseAdapter {


    Context context;
    private final String[] values;
    private final int[] images;

    public GridAdapter(Context context, String[] values, int[] images) {
        this.context = context;
        this.values = values;
        this.images = images;
    }


    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_item, viewGroup, false);
        }

        ImageView imageview = (ImageView) convertView.findViewById(R.id.imageview);
        TextView textview = (TextView) convertView.findViewById(R.id.textview);

        imageview.setImageResource(images[position]);
        textview.setText(values[position]);

        return convertView;


    }
}
