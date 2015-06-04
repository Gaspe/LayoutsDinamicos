package gasparv.layoutsdinamicos;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;


public class Activity4Step extends ActionBarActivity {
    private ProgressDialog pDialog;
    private Class4step[] steps;
    public long sig_paso = 0;
    private static String url ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_);
        Bundle bundle = getIntent().getExtras();
        String i= bundle.getString("procedure");
        url="https://dynamicformapi.herokuapp.com/steps/by_procedure/"+i+".json";
        new GetData().execute();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_steps_, menu);
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
    public class GetData extends AsyncTask<Void, Void, Void> {
        DataEntry dataEntry = new DataEntry();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Activity4Step.this);
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
                    JSONArray jsonarray = new JSONArray(jsonStr);
                    steps = new Class4step[jsonarray.length()];
                    for (int i = 0; i < jsonarray.length(); i++) {
                        steps[i] = Class4step.getStep(jsonarray.getJSONObject(i));
                    }

                } catch (JSONException e) {
                    Log.e(e.toString(), e.getMessage());
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
             *
             * Updating parsed JSON data into ListView
             * */
            /*Fragment newFragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FragmentView, newFragment,String.valueOf(0));
            ft.commit();         */
            Frag4Step frag = new Frag4Step();
            frag.step = steps[0];
            FragmentTransaction trans =  getSupportFragmentManager().beginTransaction();
            trans.add(R.id.scrollView
                    , frag, null)
                    .commit();

        }
    }
    public void Clicked(View v){
        if (sig_paso == -1) {
        this.finish();
    } else {
        Class4step s = null;
        for (Class4step step : steps) {
            if (step.step_id == sig_paso) {
                s = step;
                int j=0;
                break;
            }
        }
        if (s != null) {
            Frag4Step frag = new Frag4Step();
            frag.step = s;
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans   .replace(R.id.scrollView, frag, null)
                    .addToBackStack(null)
                    .commit();
        }
        }
    }
    @Override
    public void onBackPressed() {

    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_steps_, container, false);
            return rootView;
        }
    }
}
