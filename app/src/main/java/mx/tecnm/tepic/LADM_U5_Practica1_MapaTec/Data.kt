package mx.tecnm.tepic.LADM_U5_Practica1_MapaTec

import com.google.firebase.firestore.GeoPoint

class Data{
    var nombre : String = ""
    var cord1 : GeoPoint = GeoPoint(0.0, 0.0)
    var cord2 : GeoPoint = GeoPoint(0.0, 0.0)
    var contiene : String = ""
    override fun toString(): String {
        return nombre+"\n"+cord1.latitude+","+cord1.longitude+"\n"+
                cord2.latitude+","+cord2.longitude + "\n" + contiene
    }
    fun estoyEn(posicionActual: GeoPoint) : Boolean {
        if (posicionActual.latitude >= cord1.latitude &&
            posicionActual.latitude <= cord2.latitude){
            if (invertir(posicionActual.longitude) >= invertir(cord1.longitude) &&
                invertir(posicionActual.longitude) <= invertir(cord2.longitude)
            ) {
                return true
            }
        }
        return false
    }
    private fun invertir(valor:Double):Double{
        return valor*-1
    }
}