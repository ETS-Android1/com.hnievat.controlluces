package com.hnievat.controlluces;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class historyActivity extends AppCompatActivity {
    private RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar myToolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        rv= findViewById(R.id.rec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));

        getData();
    }

    private void getData() {
        class GetData extends AsyncTask<Void,Void, List<MyDatalist>> {

            @Override
            protected List<MyDatalist> doInBackground(Void... voids) {
                List<MyDatalist>MyDatalists=ledControl.myDatabase.myDao().getMyData();
                return MyDatalists;

            }

            @Override
            protected void onPostExecute(List<MyDatalist> MyDatalist) {
                MyAdapter adapter=new MyAdapter(MyDatalist);
                rv.setAdapter(adapter);
                super.onPostExecute(MyDatalist);
            }
        }
        GetData gd=new GetData();
        gd.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.blankmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}