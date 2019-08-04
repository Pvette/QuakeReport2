package com.example.android.quakereport2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

//public class EarthquakeActivity extends AppCompatActivity {

 //   private static final String LOG_TAG = EarthquakeActivity.class.getName();

    /** URL for earthquake data from the USGS dataset */
//    private static final String USGS_REQUEST_URL =
//            "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";

//    private EarthquakeAdapter mAdapter;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
 //       super.onCreate(savedInstanceState);
 //       setContentView(R.layout.earthquake_activity);
        //Create a fake list of earthquake locations.
 //       ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes();

        //  earthquakes.add(new Earthquake("7.2", "San Francisco", "Feb 2, 2016"));
        //  earthquakes.add(new Earthquake("6.1","London", "July 20, 2015"));
        //  earthquakes.add(new Earthquake("3.9","Tokyo", "Nov 10, 2014"));
        //  earthquakes.add(new Earthquake("5.4", "Mexico City", "May 3, 2014"));
        //  earthquakes.add(new Earthquake("2.8","Moscow", "Jan 21, 2013"));
        //   earthquakes.add(new Earthquake("4.9","Rio de Janeiro", "Aug 19, 2012"));
        //   earthquakes.add(new Earthquake("1.6","Paris", "Oct 30, 2011"));


        // Find a reference to the {@link ListView} in the layout
//        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        //     ArrayAdapter<String> adapter = new ArrayAdapter<>(
        //            this, android.R.layout.simple_list_item_1, earthquakes);

        //final EarthquakeAdapter adapter = new EarthquakeAdapter(this, earthquakes);

 //       mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

//        earthquakeListView.setAdapter(mAdapter);
  //      earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

 //           @Override
 //           public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//                Earthquake currentEarthquake = mAdapter.getItem(position);

//                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

 //               Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

 //               startActivity(websiteIntent);
 //           }
 //       });
 //   }
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
  //      earthquakeListView.setAdapter(adapter);

//    }



public class EarthquakeActivity extends AppCompatActivity
        implements androidx.loader.app.LoaderManager.LoaderCallbacks<List<Earthquake>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

        private static final String LOG_TAG = EarthquakeActivity.class.getName();

        /**
         * URL for earthquake data from the USGS dataset
         */

        private static final String USGS_REQUEST_URL =
                "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";


        /**
         * Constant value for the earthquake loader ID. We can choose any integer.
         * This really only comes into play if you're using multiple loaders.
         */

        private static final int EARTHQUAKE_LOADER_ID = 1;

        /**
         * Adapter for the list of earthquakes
         */
        private EarthquakeAdapter mAdapter;


        /**
         * TextView that is displayed when the list is empty
         */
        private TextView mEmptyStateTextView;
        private MenuItem item;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.earthquake_activity);


            // Find a reference to the {@link ListView} in the layout

            ListView earthquakeListView = (ListView) findViewById(R.id.list);

            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

            earthquakeListView.setEmptyView(mEmptyStateTextView);


            // Create a new adapter that takes an empty list of earthquakes as input

            mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());


            // Set the adapter on the {@link ListView}

            // so the list can be populated in the user interface

            earthquakeListView.setAdapter(mAdapter);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            prefs.registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);

            // Set an item click listener on the ListView, which sends an intent to a web browser

            // to open a website with more information about the selected earthquake.

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // Find the current earthquake that was clicked on
                    Earthquake currentEarthquake = mAdapter.getItem(position);

                    // Convert the String URL into a URI object (to pass into the Intent constructor)

                    Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                    // Create a new intent to view the earthquake URI

                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                    // Send the intent to launch a new activity

                    startActivity(websiteIntent);

                }

            });

            // Get a reference to the ConnectivityManager to check state of network connectivity

            ConnectivityManager connMgr = (ConnectivityManager)

                    getSystemService(Context.CONNECTIVITY_SERVICE);

            // Get details on the currently active default data network

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


            // If there is a network connection, fetch data

            if (networkInfo != null && networkInfo.isConnected()) {

                // Get a reference to the LoaderManager, in order to interact with loaders.

                android.app.LoaderManager loaderManager = getLoaderManager();


                // Initialize the loader. Pass in the int ID constant defined above and pass in null for

                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid

                // because this activity implements the LoaderCallbacks interface).

                loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, (android.app.LoaderManager.LoaderCallbacks<Object>) this);

            } else {

                // Otherwise, display error

                // First, hide loading indicator so error message will be visible

                View loadingIndicator = findViewById(R.id.loading_indicator);

                loadingIndicator.setVisibility(View.GONE);

                // Update empty state with no connection error message

                mEmptyStateTextView.setText(R.string.no_internet_connection);

            }

        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals(getString(R.string.settings_min_magnitude_key)) ||
                    key.equals(getString(R.string.settings_order_by_key))) {

                // Clear the ListView as a new query will be kicked off
                mAdapter.clear();

                // Hide the empty state text view as the loading indicator will be displayed
                mEmptyStateTextView.setVisibility(View.GONE);

                // Show the loading indicator while new data is being fetched

                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                // Restart the loader to requery the USGS as the query settings have been updated
                getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, (LoaderManager.LoaderCallbacks<Object>) this);
            }
        }

        @Override

        public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String minMagnitude = sharedPrefs.getString(
                    getString(R.string.settings_min_magnitude_key),
                    getString(R.string.settings_min_magnitude_default));

            String orderBy = sharedPrefs.getString(
                    getString(R.string.settings_order_by_key),
                    getString(R.string.settings_order_by_default)
            );


            Uri baseUri = Uri.parse(USGS_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            uriBuilder.appendQueryParameter("format", "geojson");
            uriBuilder.appendQueryParameter("limit", "10");
            uriBuilder.appendQueryParameter("minmag", minMagnitude);
            uriBuilder.appendQueryParameter("orderby", orderBy);

            // Create a new loader for the given URL
            return new EarthquakeLoader(this, uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

            // Hide loading indicator because the data has been loaded

            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setText(R.string.no_earthquakes);


            // Clear the adapter of previous earthquake data

            //mAdapter.clear();


            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's

            // data set. This will trigger the ListView to update.

            if (earthquakes != null && !earthquakes.isEmpty()) {
                //mAdapter.addAll(earthquakes);
                updateUi(earthquakes);
            }
        }

        private void updateUi(List<Earthquake> earthquakes) {
        }

        public void onLoaderReset(Loader<List<Earthquake>> loader, Menu menu) {
            // Loader reset, so we can clear out our existing data.
            mAdapter.clear();


            @Override
            public boolean onCreateOptionMenu (Menu menu){
                getMenuInflater().inflate(R.menu.main, menu);
                return true;
            }


            @Override

            public boolean onOptionsItemSelected (MenuItem item){
                int id = item.getItemId();
                if (id == R.id.action_settings) {
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    return true;
                }
                return super.onOptionsItemSelected(item);

                //    private class EarthquakeLoader extends Loader<List<Earthquake>> {
                //    public EarthquakeLoader(EarthquakeActivity earthquakeActivity, String usgsRequestUrl) {
                //        super();
                //    }
            }
        }
    }