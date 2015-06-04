package gasparv.layoutsdinamicos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PaginaPrincipal extends ActionBarActivity {
    private ProgressDialog pDialog;
    private ListView list;
    private static String url = "http://dynamicformapi.herokuapp.com/groups.json";
    JSONArray grupos = null;
    ArrayList<DataEntry> listagrupos;
    ArrayList<String> namegrupos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);
        listagrupos = new ArrayList<DataEntry>();
        list = (ListView) findViewById(R.id.listView);
        namegrupos = new ArrayList<String>();
        new GetData().execute();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                //Alternativa 1:
                int opcionSeleccionada = position;
                Intent i = new Intent(PaginaPrincipal.this, Procedures_act.class);
                i.putExtra("opcion",opcionSeleccionada);
                startActivity(i);
                //Alternativa 2:
                //String opcionSeleccionada =
                //      ((TextView)v.findViewById(R.id.LblTitulo))
                //          .getText().toString();
            }
        });
    }

    public void requestData(View view) {

        new GetData().execute();


    }
    private class GetData extends AsyncTask<Void, Void, Void> {
        DataEntry dataEntry = new DataEntry();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PaginaPrincipal.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            if (jsonStr != null) {
                try {
                    grupos = new JSONArray(jsonStr);
                    for (int i = 0; i < grupos.length(); i++) {
                        JSONObject c = grupos.getJSONObject(i);
                        DataEntry dataEntry = new DataEntry();
                        dataEntry.setname(c.getString("name"));
                        dataEntry.setid(c.getString("id"));
                        listagrupos.add(dataEntry);
                        namegrupos.add(dataEntry.getname());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>(PaginaPrincipal.this,
                    R.layout.list_item_first, namegrupos);
            list.setAdapter(adapter);

            /*Fragment newFragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FragmentView, newFragment,String.valueOf(0));
            ft.commit();         */}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pagina_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

}
