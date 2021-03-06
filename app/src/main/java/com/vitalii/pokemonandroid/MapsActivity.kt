package com.vitalii.pokemonandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadPockemons()

    }
var ACCESSLOCATION=123 //Code of permission to request this
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }
        GetUserLocation()
    }

    /**
     * Getting result of permission request
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            ACCESSLOCATION->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                }else{
                    Toast.makeText(this,"We can't access your location",Toast.LENGTH_SHORT).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    fun GetUserLocation(){
        Toast.makeText(this,"User location is on",Toast.LENGTH_SHORT).show()
        //TODO: Will implement later

        val myLocation = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)
        val myThread = MyThread()
        myThread.start()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    //get user location

    var location:Location? = null

    /**
     * Sets current user location to variable
     */
    inner class MyLocationListener:LocationListener{
        constructor(){
            location = Location("Start")
            location!!.latitude=0.0
            location!!.longitude=0.0
        }
        override fun onLocationChanged(p0: Location?) {
            location = p0
        }
        override fun onStatusChanged(p0: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        override fun onProviderEnabled(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        override fun onProviderDisabled(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    /**
     * Every second show current user location on the map
     */
    var oldLocation:Location? = null
    inner class MyThread:Thread{
        constructor():super() {
            oldLocation = Location("Start")
            oldLocation!!.latitude =0.0
            oldLocation!!.longitude =0.0
        }
        override fun run() {
            while (true){
                try {
                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }
                    oldLocation=location
                    runOnUiThread {
                        mMap.clear()
                        //Show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet("Here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        //Show Pokemons

                        for (pokemon in 0..listPokemon.size-1){
                            val newPokemon = listPokemon[pokemon]
                            if(!newPokemon.isCatched){
                                val pokemonLocation = LatLng(newPokemon.location!!.latitude, newPokemon.location!!.longitude)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(pokemonLocation)
                                        .title(newPokemon.name)
                                        .snippet("${newPokemon.des}, power:${newPokemon.power}")
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))

                                if(location!!.distanceTo(newPokemon.location)<2){
                                    newPokemon.isCatched = true
                                    listPokemon[pokemon] = newPokemon
                                    playerPower += newPokemon.power!!
                                    Toast.makeText(applicationContext,"You catch new Pokemon. Your power is $playerPower",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    Thread.sleep(1000)
                }catch (ex:Exception){}
            }
        }
    }

    var playerPower = 0.0
    var listPokemon = ArrayList<Pokemon>()

    /**
     * Add pockemons to game
     */
    private fun loadPockemons(){
        listPokemon.add(Pokemon(R.drawable.bulbasaur, "Bulbasar", "description", 55.0, 37.334, -122.0))
        listPokemon.add(Pokemon(R.drawable.charmander, "Charmander", "description", 90.5, 37.34, -122.0))
        listPokemon.add(Pokemon(R.drawable.squirtle, "Squirtle", "description", 33.0,  37.35, -122.0))
    }
}
