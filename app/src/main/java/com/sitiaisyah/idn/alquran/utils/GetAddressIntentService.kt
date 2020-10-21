package com.sitiaisyah.idn.alquran.utils

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import java.util.*

class GetAddressIntentService : IntentService(IDENTIFIER) {
    private var addressResultreceiver: ResultReceiver? = null

    override fun onHandleIntent(p0: Intent?) {
        var msg = ""

        //get result receiver from intent
        addressResultreceiver = p0!!.getParcelableExtra("add_receiver")
        if (addressResultreceiver == null) {
            return
        }
        val location = p0.getParcelableExtra<Location>("add_location")

        if (location == null) {
            msg = "No location, can't go further without location"
            sendResultsToReceiver(0, msg)
            return
        }
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
        } catch (ignored: Exception) {

        }
        if (addresses == null || addresses.size == 0) {
            msg = "No address found for the location"
            sendResultsToReceiver(1, msg)
        } else {
            val address = addresses[0]
            val addressDetails = StringBuffer()

            /*addressDetails.append(address.getFeatureName());
            addressDetails.append("\n");

            addressDetails.append(address.getLocality());
a           ddressDetails.append("\n");

            addressDetails.append(address.getSubAdminArea());
            addressDetails.append("\n");

            addressDetails.append(address.getPostalCode());
            addressDetails.append("\n");

            addressDetails.append(address.getThoroughfare());
            addressDetails.append("\n");

            addressDetails.append(address.getCountryName());
            addressDetails.append("\n");*/

            addressDetails.append(address.adminArea)
            addressDetails.append("\n")
            sendResultsToReceiver(2, addressDetails.toString())
        }
    }

    private fun sendResultsToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle()
        bundle.putString("address_result", message)
        addressResultreceiver!!.send(resultCode, bundle)
    }

    companion object {
        private const val IDENTIFIER = "GetAddressIntentService"
    }
}