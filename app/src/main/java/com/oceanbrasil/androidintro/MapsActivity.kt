package com.oceanbrasil.androidintro

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oceanbrasil.androidintro.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))

        val ocean = LatLng(-3.0924037665879474, -60.01862751890051)
        mMap.addMarker(MarkerOptions().position(ocean).title("OCEAN EST"))
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(ocean))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ocean, 15f))

        startLocationService()

    }

    private fun startLocationService() {
        // Checa se tem a permissão de acessar a localização
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Exibe uma mensagem solicitando a permissão da localização
            Toast.makeText(this, "Permita a localização.", Toast.LENGTH_LONG).show()

            // Solicitar a permissão
            requestLocationPermission()

            // Encerra a execução da função, pois não temos permissão para ver a localização
            return
        }

        // Acessar o serviço de localização do dispositivo
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Definimos um provedor de localização, geralmente NETWORK (placa de internet) ou GPS (localização)
        val locationProvider = LocationManager.NETWORK_PROVIDER

        // Pegamos a localização atual (que é a última localização conhecida pelo provedor)
        val currentLocation = locationManager.getLastKnownLocation(locationProvider)

        // Caso não tenha localização conhecida
        if (currentLocation == null) {
            // Exibe uma mensagem dizendo que não encontrou a localização
            Toast.makeText(this, "Nenhuma localização encontrada.", Toast.LENGTH_LONG).show()

            // Encerramos a execução da função, pois não tem localização conhecida
            return
        }

        // Adicionamos uma marca e movemos a câmera para a localização atual do sensor
        val currentLocationLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        mMap.addMarker(MarkerOptions().position(currentLocationLatLng).title("Você está aqui!"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocationLatLng))
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            // Activity que está solicitando, pois ela receberá o resultado
            this,
            // Lista de permissões que serão solicitadas
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            // Um código numérico qualquer para sabermos a origem da solicitação
            1
        )
    }

    // Preparamos o resultado das permissões solicitadas
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1
            && (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            startLocationService()
        }
    }
}