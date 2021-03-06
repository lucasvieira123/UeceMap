package com.example.lucas.uecemap;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class
        PesquisaActivity extends AppCompatActivity {
    private static final int URL_LOADER = 0;
    private MyDatabaseHelper db = new MyDatabaseHelper(this);
    private LugarDAOSQLLite lugarDAO = new LugarDAOSQLLite(db);
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CustomTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa);
        handleIntent(getIntent());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle(R.string.title);
       SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query, "Busca não encontrou nenhum resultado");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText, null);
                return false;
            }
        });
        return true;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        //faz trata a pesquisa
        handleIntent(intent);

    }
    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query,null);


        }
    }
    private void search(String query, String resposta){
        final List<Lugar> lugarList = lugarDAO.findByNome(query);
        ListView lv = (ListView) findViewById(R.id.listPesq);
        if(lugarList.size() == 0 && resposta!=null)
            showToast(resposta);
        else {
            adapter = new MyAdapter(this, lugarList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    goToMap(lugarList.get(position).getLongitude(), lugarList.get(position).getLatitude(),lugarList.get(position).getId());
                }
            });
        }
    }
    public void showToast(String mensagem){
        Toast toast = Toast.makeText(this, mensagem, Toast.LENGTH_SHORT);
        toast.show();
    }
    public void goToMap(Double longi, Double lat, int id){
        Bundle b = new Bundle();
        b.putDouble("long", longi);
        b.putDouble("lat", lat);
        b.putInt("id", id);
        Intent i = new Intent(this,MapsActivity.class);
        i.putExtras(b);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }
}