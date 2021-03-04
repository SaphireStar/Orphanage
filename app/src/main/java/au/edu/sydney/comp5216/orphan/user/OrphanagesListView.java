package au.edu.sydney.comp5216.orphan.user;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.user.profile.Profile;

/**
 * Displays the list of all orphanages and allow users to search for a certain orphanage
 * @Author: Yiqing Yang
 */
public class OrphanagesListView extends AppCompatActivity {
    // widgets
    private SearchView sv;

    // listView
    private ItemAdapter adapter;
    private ArrayList<Orphanage> list;
    private ArrayList<Orphanage> allOrphanages;
    private ListView lv;
    // user information
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_orphanages);

        // get the user id
        userId = getIntent().getStringExtra("userId");

        // fetch the list of all orphanages
        allOrphanages = new ArrayList<>();
        list = new ArrayList<>();


        int network = checkNetworkConnection();
        Log.i("Network Type", " " + network);
        if(network== 2){
            try {
                readFromLocalCache(allOrphanages);
                list.addAll(allOrphanages);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            fetchAllOrphnages(allOrphanages);
        }

        // set up the list view
        lv = (ListView) findViewById(R.id.list_item);
        adapter = new ItemAdapter(this, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // start an activity to display the orphanage profile
                Intent intent = new Intent(
                        OrphanagesListView.this, UserOrphProfile.class);
                intent.putExtra("userId", userId);
                intent.putExtra("id", list.get(i).getId());
                intent.putExtra("name", list.get(i).getName());
                intent.putExtra("address", list.get(i).getAddress());
                intent.putExtra("description", list.get(i).getDescription());
                intent.putExtra("image", list.get(i).getImage());
                intent.putExtra("document", list.get(i).getDocumentId());
                startActivity(intent);
            }
        });

        // set up search view
        setUpSearchBar();
    }

    /**
     * Sets up the search bar
     */
    private void setUpSearchBar() {
        sv = (SearchView) findViewById(R.id.search_bar);
        sv.setSubmitButtonEnabled(true);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                // empty the list
                list.clear();

                // add the results
                for (Orphanage o : allOrphanages) {
                    if (o.getName().contains(s)) {
                        list.add(o);
                    }
                }

                // update the adapter
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // recover the list when the key world is empty
                if (s.length() == 0) {
                    list.clear();

                    // add the results
                    for (Orphanage o : allOrphanages) {
                        if (o.getName().contains(s)) {
                            list.add(o);
                        }
                    }
                    // update the adapter
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

    }

    /**
     * Fetches the list of all orphanages
     * @param array the array used to store all orphanages
     */
    private void fetchAllOrphnages(final ArrayList<Orphanage> array){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orph")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // add the orphanage to a list
                                Orphanage orphanage = new Orphanage();
                                orphanage.setId(document.get("id").toString());
                                orphanage.setName(document.get("name").toString());
                                orphanage.setAddress(document.get("address").toString());
                                orphanage.setDescription(document.get("description").toString());
                                orphanage.setImage(document.get("image").toString());
                                orphanage.setDocumentId(document.getId());

                                // update local cache
                                try {
                                    saveToLocalCache(orphanage);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // firebase
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                StorageReference islandRef = storageRef.child(orphanage.getImage());

                                File localFile = new File(getFilesDir(),
                                        orphanage.getId()+orphanage.getImage().substring(
                                                orphanage.getImage().lastIndexOf('.')));
                                if(!localFile.exists()){
                                    try {
                                        localFile.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    islandRef.getFile(localFile).addOnSuccessListener(
                                            new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.
                                                                      TaskSnapshot taskSnapshot) {
                                            // Local temp file has been created
                                            adapter.notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });
                                }
                                array.add(orphanage);
                            }
                            //
                            list.addAll(allOrphanages);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    /**
     * Writes orphanages to local folders in a customized form to save the storage
     * @param orphanage the orphanage to be saved
     */
    private void saveToLocalCache(Orphanage orphanage) throws IOException {
        // create file
        File file = new File(getFilesDir(), orphanage.getId()+".orp");
        file.delete();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // identify the path
        String filePathName = getFilesDir().getAbsolutePath()
                + File.separator + orphanage.getId()+".orp";

        // write to the file
        FileOutputStream fos = new FileOutputStream(filePathName);
        StringBuilder sb = new StringBuilder();
        sb.append(orphanage.getId());
        sb.append("|" + orphanage.getName());
        sb.append("|" + orphanage.getAddress());
        sb.append("|" + orphanage.getDescription());
        sb.append("|" + orphanage.getImage());
        sb.append("|" + orphanage.getDocumentId());
        fos.write(sb.toString().getBytes());
        fos.close();
    }

    /**
     * Reads from the local cache to fetch the list of orphanages
     * @param array the array used to store the list
     */
    private void readFromLocalCache(ArrayList<Orphanage> array) throws IOException {
        // identify the folder path
        File folder = getFilesDir().getAbsoluteFile();

        // list all files
        for(File file: folder.listFiles()){
            // find out the data file
            if(file.getName().endsWith(".orp")){
                String filePathName = getFilesDir().getAbsolutePath()
                        + File.separator + file.getName();

                // read from file
                StringBuffer sb = new StringBuffer();
                FileInputStream fis = new FileInputStream(filePathName);
                byte[] buffer = new byte[1024];
                int len = fis.read(buffer);
                while (len != -1 ) {
                    sb.append(new String(buffer, 0, len));
                    len = fis.read(buffer);
                }
                fis.close();

                // transfer into Orphanage object
                String info = sb.toString();
                String[] elements = info.split("\\|");
                Orphanage orphanage = new Orphanage();
                orphanage.setId(elements[0]);
                orphanage.setName(elements[1]);
                orphanage.setAddress(elements[2]);
                orphanage.setDescription(elements[3]);
                orphanage.setImage(elements[4]);
                orphanage.setDocumentId(elements[5]);

                // add to the arraylist
                array.add(orphanage);
            }
        }
    }

    /**
     * Checks the current network, returns whether it is Wifi or a cellular network
     */
    public int checkNetworkConnection() {
        // get wifi information
        ConnectivityManager conManager = (ConnectivityManager)getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi =conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // get cellular information
        NetworkInfo mobile =conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        // return the type of network connection
        if(wifi.isAvailable()){
            return 1;
        }else {
            assert mobile != null;
            if(mobile.isAvailable()){
                return 2;
            }
        }
        // if the phone is not connecting to wifi or cellular
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_note:
                profile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void profile(){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
}
