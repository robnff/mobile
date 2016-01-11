package br.ufpe.cin.openredu.activities.adressAddOn;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import bap2.brunoassispereira.R;

public class MapActivity extends FragmentActivity {

    /* O objetivo dessa activity e ser o primeiro passo para o usuário adicionar um local a sua
    atividade no OpenREDU mobile. Aqui ele selecionara um ponto no mapa para dar inicio ao processo.
    Como nossa feature e orientada a localizacao, tudo depende do lugar que ele selecionar
     */

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mainMarker;
    private List<Address> addresses = null;
    //private Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    private static final LatLng INITIAL_POSITION = new LatLng(-8.062964, -34.871442);

    public final static String EXTRA_LATITUDE = "bap2.myfirstapp.LATITUDE";
    public final static String EXTRA_LONGITUDE = "bap2.myfirstapp.LONGITUDE";
    public final static String EXTRA_ADDRESSES = "bap2.myfirstapp.ADDRESSES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //chamada do metodo de inicializar o mapa
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    /*
    metodo padrao para implementacao do mapa e definicao de algumas caracteristicas basicas
    dele na tela
     */
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        UiSettings mapSettings = mMap.getUiSettings();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng position){
                mainMarker.setPosition(position);
                getAddress();
            }
        });

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setTiltGesturesEnabled(false);
        mapSettings.setCompassEnabled(false);
        mapSettings.setMapToolbarEnabled(false);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(INITIAL_POSITION)
                .zoom(10)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mainMarker = mMap.addMarker(new MarkerOptions()
                .position(INITIAL_POSITION)
                .title("Selected Location")
                .draggable(true));

        getAddress();
    }

    /** Called when the user clicks the Search button */
    //metodo chamado quando o usuario clica no botao de busca
    public void searchLocation(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SearchLocationActivity.class);
        intent.putExtra(EXTRA_LATITUDE, mainMarker.getPosition().latitude);
        intent.putExtra(EXTRA_LONGITUDE, mainMarker.getPosition().longitude);
        intent.putExtra(EXTRA_ADDRESSES, (Serializable) addresses);

        startActivity(intent);
    }


    //metodo que retorna o endereco do local clicado, esse endereco e usado na proxima activity
    public void getAddress()
    {
        try {
            Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(
                    mainMarker.getPosition().latitude
                    ,mainMarker.getPosition().longitude
                    ,5);
            addresses.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
