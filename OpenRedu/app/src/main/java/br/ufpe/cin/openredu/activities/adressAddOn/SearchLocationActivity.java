package br.ufpe.cin.openredu.activities.adressAddOn;

import android.content.Intent;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.List;

import bap2.brunoassispereira.R;
import bap2.brunoassispereira.support.Location;

public class SearchLocationActivity extends AppCompatActivity {

    /*
    Essa activity foi criada para evitar erros e facilitar a escolha de locais por parte do usuario.
    Ao clicar no mapa na MapActivity, o programa busca endereços proximos e lista eles para o
    usuario escolher. A ideia e livrar o usuario da necessidade de ser muito preciso no clique e
    garantir que possamos referenciar todo e qualquer clique a um lugar especifico no google maps.
     */

    public final static String EXTRA_ADDRESS = "bap2.myfirstapp.ADDRESS";
    public final static String EXTRA_CITY = "bap2.myfirstapp.CITY";
    public final static String EXTRA_STATE = "bap2.myfirstapp.STATE";
    public final static String EXTRA_POSTAL_CODE = "bap2.myfirstapp.POSTAL_CODE";
    //public String[] values;

    private ListView citiesListView;
    private Location[] cities = null;
    private JSONObject jsonResult;
    private ArrayAdapter<String> adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        citiesListView = (ListView) findViewById(R.id.lvcities);

        //Get the message from the intent
        /*
        aqui os dados vindos da MapActivity são coletados e uma lista de enderecos proximos daquela
        latitude e longitude e criada
         */
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra(MapActivity.EXTRA_LATITUDE, 0);
        double longitude = intent.getDoubleExtra(MapActivity.EXTRA_LONGITUDE, 0);
        List<Address> addresses = (List<Address>) intent.getSerializableExtra(MapActivity
                .EXTRA_ADDRESSES);

        String[] values = new String[addresses.size()];
        final Location[] locations = new Location[addresses.size()];
        String address
                ,city
                ,state
                ,country
                ,postalCode
                ,knownName
                ,fullAddress;

        for (int i=0; i < addresses.size(); i++)
        {
            address = addresses.get(i).getAddressLine(0);
            city = addresses.get(i).getLocality();
            state = addresses.get(i).getAdminArea();
            country = addresses.get(i).getCountryName();
            postalCode = addresses.get(i).getPostalCode() != null ? "CEP: " + addresses.get(i).getPostalCode(): "";
            knownName = addresses.get(i).getFeatureName();

            fullAddress = "Endereço: " + address + "\nCidade: " + city + "\nEstado: " + state +
                    "\n" + postalCode;

            System.out.print(fullAddress);

            locations[i] = new Location(address, city, state, postalCode);
            values[i] = fullAddress;
        }

        adapter = new ArrayAdapter<String>(SearchLocationActivity.this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                values);

        citiesListView.setAdapter(adapter);

        /*
        esse metodo envia para a proxima activity todos os dados referentes ao item da lista que
        foi clicado pelo usuario
        */
        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), LocationActivity.class);
                intent.putExtra(EXTRA_ADDRESS, locations[position].getAddress());
                intent.putExtra(EXTRA_CITY, locations[position].getCity());
                intent.putExtra(EXTRA_STATE, locations[position].getState());
                intent.putExtra(EXTRA_POSTAL_CODE, locations[position].getPostalCode());
                startActivity(intent);
            }
        });

        //new GetJSONTask().execute(latitude, longitude);

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

    /*private class GetJSONTask extends AsyncTask<Double, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Double... params) {
            String[] values;
            JSONObject jsonResult = getJSON(params[0], params[1]);
            try{
                JSONArray jsonCityList = jsonResult.getJSONArray("list");

                cities = new Location[jsonCityList.length()];
                values = new String[jsonCityList.length()];

                for (int i = 0; i < jsonCityList.length(); i++){
                    JSONObject jsonCityInfo = jsonCityList.getJSONObject(i);

                    String name = jsonCityInfo.getString("name");

                    JSONArray jsonWeather = jsonCityInfo.getJSONArray("weather");
                    JSONObject jsonDescr = jsonWeather.getJSONObject(0);
                    String description = jsonDescr.getString("description");

                    JSONObject jsonMain = jsonCityInfo.getJSONObject("main");
                    double tempMin = (jsonMain.getDouble("temp_min"));
                    double tempMax = (jsonMain.getDouble("temp_max"));

                    cities[i] = new Location(name, tempMax, tempMin, description);
                    values[i] = name;
                }

                adapter = new ArrayAdapter<String>(SearchLocationActivity.this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        values);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonResult;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (adapter != null) citiesListView.setAdapter(adapter);

            citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(), LocationActivity.class);
                    intent.putExtra(EXTRA_CITY_NAME, cities[position].getName());
                    //intent.putExtra(EXTRA_MAX_TEMP, cities[position].getTempMax());
                    //intent.putExtra(EXTRA_MIN_TEMP, cities[position].getTempMin());
                    intent.putExtra(EXTRA_DESCRIPTION, cities[position].getDescription());
                    startActivity(intent);
                }
            });
        }
    }*/

    /*public JSONObject getJSON(double latitude, double longitude) {
        String urlText = "http://api.openweathermap.org/data/2.5/find?lat=" + latitude +
                "&lon=" + longitude + "&cnt=15&units=metric";
        StringBuilder result = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlText);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            jsonResult = new JSONObject(result.toString());
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return jsonResult;
    }*/
}
