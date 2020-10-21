package com.sitiaisyah.idn.alquran.activity

import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sitiaisyah.idn.alquran.R
import com.sitiaisyah.idn.alquran.utils.ParserPlace
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.jar.Manifest

class MasjidActivity : AppCompatActivity(), LocationListener {
    var mGoogleMap : GoogleMap? = null
    var toolbar : androidx.appcompat.widget.Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masjid)

        toolbar = findViewById(R.id.toolbal_masjid)
        setSupportActionBar(toolbar)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = null

        val fragment = supportFragmentManager.findFragmentById(R.id.fMap)
        as SupportMapFragment?
        fragment!!.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            initMap()
        }
    }

    private fun initMap() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            155
            )
            return
        }

        if (mGoogleMap != null){
            mGoogleMap!!.isMyLocationEnabled = true
            val locationManager = getSystemService(Context.LOCATION_SERVICE)
            as LocationManager

            val criteria = Criteria()
            val provider = locationManager
                .getBestProvider(criteria, true)
            val location  = locationManager
                .getLastKnownLocation(provider!!)
            if (location != null){
                onLocationChanged(location)
            }else locationManager.requestLocationUpdates(provider, 20000, 0f, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val long = location.longitude
        val latlng = LatLng(lat, long)
        mGoogleMap!!.moveCamera(CameraUpdateFactory
            .newLatLng(latlng))
        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(12f))
        val sb = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" +  lat + "," + long
                "&radius=20000" +
                "&types=" + "mosque" +
                "&key=" + resources.getString(R.string.google_maps_key)
        PlacesTask().execute(sb)

    }

    private inner class PlacesTask: AsyncTask<String?, Int?, String?>() {
        override fun doInBackground(vararg url: String?): String? {
            var data : String? = null
            try {
                data = downloadURL(url[0].toString())
            }catch (e: Exception){
                e.printStackTrace()
            }
            return data
        }

        override fun onPostExecute(result: String?) {
            ParserTask().execute(result)
        }

    }

    private inner class ParserTask : AsyncTask<String?, Int?, List<HashMap<String, String>>?>() {
        var jObject : JSONObject? = null
        override fun doInBackground(vararg jsonData: String?): List<HashMap<String, String>>? {
            var places : List<HashMap<String, String>>? = null
            var parcerPlaces = ParserPlace()
            try {
                jObject = JSONObject(jsonData[0])
                places = parcerPlaces.parse(jObject!!)
            }catch (e: Exception){
                e.printStackTrace()
            }
            return places
        }

        override fun onPostExecute(list: List<HashMap<String, String>>?) {
            mGoogleMap!!.clear()
            for (i in list!!.indices){
                val markerOptions = MarkerOptions()
                val hmPlace = list[i]
                val pinDrop = BitmapDescriptorFactory.fromResource(R.drawable.ic_place)
                val lat = hmPlace["lat"]!!.toDouble()
                val lng = hmPlace["lng"]!!.toDouble()
                val nama = hmPlace["place_name"]
                val namaJln = hmPlace["vicinty"]
                val latlng = LatLng(lat, lng)
                markerOptions.icon(pinDrop)
                markerOptions.position(latlng)
                markerOptions.title("$nama : $namaJln")
                mGoogleMap!!.addMarker(markerOptions)
            }
        }

    }

    private fun downloadURL(strUrl: String): String {
        var data = ""
        val iStream: InputStream
        val urlConection: HttpURLConnection
        try {
            val url = URL(strUrl)
            urlConection = url.openConnection() as HttpURLConnection
            urlConection.connect()
            iStream = urlConection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuilder()
            var line : String?
            while (br.readLine().also { line = it } != null){
                sb.append(line)
            }
            data = sb.toString()
            br.close()
            iStream.close()
            urlConection.disconnect()
        }catch (e : Exception){
            e.printStackTrace()
        }
        return data
    }

    override fun onStatusChanged(s: String?, i: Int, bundle: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}