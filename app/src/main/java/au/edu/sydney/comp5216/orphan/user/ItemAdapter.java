package au.edu.sydney.comp5216.orphan.user;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.StorageLocation;

/**
 * ArrayAdapter for Orphanage
 * @Author: Yiqing Yang
 */
public class ItemAdapter extends ArrayAdapter<Orphanage> {
    public ItemAdapter(Context context, ArrayList<Orphanage> orphanages) {
        super(context, 0, orphanages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.orph_list_view, parent,
                    false);
        }

        // get the data item for this position
        Orphanage orphanage = getItem(position);

        // widgets
        final ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView description = (TextView) convertView.findViewById(R.id.description);

        name.setText(orphanage.getName());
        description.setText(orphanage.getDescription());
        File f = new File(StorageLocation.storagePath+orphanage.getId()
                +orphanage.getImage().substring(orphanage.getImage().lastIndexOf('.')));
        Log.i("Local image", ""+ f.exists());
        image.setImageURI(Uri.fromFile(f));
        // return the completed view to render on screen
        return convertView;
    }
}
