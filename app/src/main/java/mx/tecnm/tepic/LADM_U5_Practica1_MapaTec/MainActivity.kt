package mx.tecnm.tepic.LADM_U5_Practica1_MapaTec

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    var siPermiso = 98
    lateinit var locacion : LocationManager
    var c1 : Location = Location("")
    var c2 : Location = Location("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),siPermiso)
        } else {
            locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var oyente = Oyente(this)
            locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, oyente)
        }
        baseRemota.collection("ITT")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        lista.setText("ERROR: "+firebaseFirestoreException.message)
                        return@addSnapshotListener
                    }
                    var resultado = ""
                    posicion.clear()
                    for(document in querySnapshot!!){
                        var data = Data()
                        data.nombre = document.getString("nombre").toString()
                        data.cord1 = document.getGeoPoint("cord1")!!
                        data.cord2 = document.getGeoPoint("cord2")!!
                        resultado += data.toString()+"\n"
                        posicion.add(data)
                    }
                    lista.setText(resultado)
                }
        btnBusqueda.setOnClickListener {
            if(busqueda.text.toString() == ""){
                ubicacion.setText("")
            }
            baseRemota.collection("ITT")
                    .whereEqualTo("nombre", busqueda.getText().toString())
                    .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                        if(firebaseFirestoreException != null){
                            ubicacion.setText("Imposible establecer conexión a FireBase")
                            return@addSnapshotListener
                        }
                        for(document in  querySnapshot!!){
                            c1.longitude = document.getGeoPoint("cord1")!!.longitude
                            c1.latitude = document.getGeoPoint("cord1")!!.latitude
                            c2.longitude = document.getGeoPoint("cord2")!!.longitude
                            c2.latitude = document.getGeoPoint("cord2")!!.latitude
                        }
                        var r = "(${(c1.latitude)}, ${c1.longitude}),(${c2.latitude}, ${c2.longitude})"
                        ubicacion.setText(r)
                    }
        }
    }
}

class Oyente(puntero:MainActivity) : LocationListener {
    var p = puntero
    override fun onLocationChanged(location: Location) {
        p.coordenadasView.setText("Coordenadas:\n${location.latitude}, ${location.longitude}")
        var posicionActual = GeoPoint(location.latitude, location.longitude)
        for (item in p.posicion) {
            if (item.estoyEn(posicionActual)) {
                p.ubicado.setText("Usted se encuentra en:\n${item.nombre}")
            }
        }
    }
}