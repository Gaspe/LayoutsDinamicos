package gasparv.layoutsdinamicos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Procedures_act extends ActionBarActivity {

    private ProgressDialog pDialog;
    private ListView list;
    private static String url ;
    JSONArray proce = null;
    ArrayList<DataEntry> listaproce;
    ArrayList<String> nameproce;
    ArrayList<String> ID_proce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedures_act);
        Bundle bundle = getIntent().getExtras();
        int i= bundle.getInt("opcion")+1;
        listaproce = new ArrayList<DataEntry>();
        list = (ListView) findViewById(R.id.listView);
        nameproce = new ArrayList<String>();
        ID_proce = new ArrayList<String>();
        url= "https://dynamicformapi.herokuapp.com/procedures/by_group/"+i+".json";
        new GetData().execute();
        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                JSONObject opcionSeleccionada = ();
                Intent i = new Intent(Procedures_act.this, Steps_Act.class);
                i.putExtra("opcion",opcionSeleccionada);
                startActivity(i);
            }
        });*/
    }

    public void requestData(View view) {

        new GetData().execute();


    }
    private void ITEMCLICK() {
        final Context c = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Procedures_act.this, Activity4Step.class);

                i.putExtra("procedure", ID_proce.get(position));
                c.startActivity(i);
            }
        });
    }
    private class GetData extends AsyncTask<Void, Void, Void> {
        DataEntry dataEntry = new DataEntry();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Procedures_act.this);
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
                    proce = new JSONArray(jsonStr);
                    for (int i = 0; i < proce.length(); i++) {
                        JSONObject c = proce.getJSONObject(i);
                        DataEntry dataEntry = new DataEntry();
                        dataEntry.setname(c.getString("name"));
                        dataEntry.setid(c.getString("description"));
                        listaproce.add(dataEntry);
                        nameproce.add(dataEntry.getname());
                        ID_proce.add(c.getString("procedure_id"));
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
            adapter = new ArrayAdapter<String>(Procedures_act.this,
                    R.layout.list_item_first, nameproce);
            list.setAdapter(adapter);
            ITEMCLICK();
            /*Fragment newFragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FragmentView, newFragment,String.valueOf(0));
            ft.commit();         */}
    }
}
