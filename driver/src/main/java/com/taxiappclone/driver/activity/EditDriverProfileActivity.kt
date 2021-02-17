package com.taxiappclone.driver.activity

import android.Manifest
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rengwuxian.materialedittext.MaterialEditText
import com.taxiappclone.common.activity.SelectPhotoActivity
import com.taxiappclone.common.app.AppConfig
import com.taxiappclone.common.app.CommonUtils
import com.taxiappclone.common.helper.SessionManager
import com.taxiappclone.common.model.Driver
import com.taxiappclone.common.model.Vehicle
import com.taxiappclone.common.model.VehicleType
import com.taxiappclone.common.network.APIService
import com.taxiappclone.common.network.ApiUtils
import com.taxiappclone.common.utils.PermissionManager
import com.taxiappclone.common.utils.ServerUtilities
import com.taxiappclone.common.view.CircleImageView
import com.taxiappclone.driver.R
import com.taxiappclone.driver.app.AppController
import com.taxiappclone.driver.app.ModuleConfig
import fr.ganfra.materialspinner.MaterialSpinner
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditDriverProfileActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var actionBar: ActionBar? = null
    private var mRegisterTask: AsyncTask<Void, Void, String>? = null

    private var pDialog: ProgressDialog? = null
    private var session: SessionManager? = null
    var apiService: APIService? = null

    lateinit var driver: Driver
    lateinit var vehicle: Vehicle

    private lateinit var profileImage: String
    private lateinit var drivingLicenseImage: String
    private lateinit var rcImage: String
    private lateinit var insuranceImage: String
    private lateinit var policeImage: String

    private lateinit var ivProfile: CircleImageView
    private lateinit var btnBrowse: AppCompatButton
    private lateinit var inputName: MaterialEditText
    private lateinit var tvDOB: AppCompatTextView
    private lateinit var spinnerGender: MaterialSpinner
    private lateinit var spinnerVehicleType: MaterialSpinner
    private lateinit var inputModel: MaterialEditText
    private lateinit var inputRegistrationNumber: MaterialEditText
    private lateinit var inputExpiry: MaterialEditText
    private lateinit var imgDrivingLicense: ImageView
    private lateinit var imgRegistrationCard: ImageView
    private lateinit var imgInsurance: ImageView
    private lateinit var imgPolice: ImageView
    private lateinit var btnSubmit: Button

    private var listVehicleTypes: List<VehicleType?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_driver_profile)

        apiService = ApiUtils.apiService

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setHomeButtonEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.title = "Edit Profile"

        pDialog = ProgressDialog(this)
        pDialog!!.setCancelable(false)
        pDialog!!.setMessage("Loading...")
        session = AppController.getInstance().sessionManager

        ivProfile = findViewById(R.id.ivProfile)
        btnBrowse = findViewById(R.id.btnBrowse)
        inputName = findViewById(R.id.inputName)
        tvDOB = findViewById(R.id.tvDOB)
        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerVehicleType = findViewById(R.id.spinnerVehicleType)
        inputModel = findViewById(R.id.inputModel)
        inputRegistrationNumber = findViewById(R.id.inputRegistrationNumber)
        inputExpiry = findViewById(R.id.inputExpiry)
        imgDrivingLicense = findViewById(R.id.imgDrivingLicense)
        imgRegistrationCard = findViewById(R.id.imgRegistrationCard)
        imgInsurance = findViewById(R.id.imgInsurance)
        imgPolice = findViewById(R.id.imgPolice)
        btnSubmit = findViewById(R.id.btnSubmit)


        if (session!!.getStringValue("vehicle_types") == "") {
            getVehicleTypes()
        } else {
            listVehicleTypes = Gson().fromJson<List<VehicleType>>(session!!.getStringValue("vehicle_types"), object : TypeToken<List<VehicleType?>?>() {}.type)
            setVehicleTypes()
        }

        getDriverDetails()
    }

    private fun getVehicleTypes() {
        showDialog()
        val header: MutableMap<String, String> = HashMap()
        header["SECURE-API-KEY"] = ModuleConfig.DRIVER_API_KEY
        mRegisterTask = object : AsyncTask<Void, Void, String>() {
            var startTime = System.currentTimeMillis()
            override fun doInBackground(vararg params: Void?): String {
                startTime = System.currentTimeMillis()
                return ServerUtilities.getServerResponse(applicationContext, ModuleConfig.URL_GET_VEHICLE_TYPES, null, header)
            }

            override fun onPostExecute(result: String?) {
                hideDialog()
                if (result != null) {
                    try {
                        val jObj = JSONObject(result)
                        Log.e("TAG", "Vehicle Types Response: $result")
                        if (jObj.optBoolean("status")) {
                            session!!.setValue("vehicle_types", jObj.getJSONArray("data").toString())
                            listVehicleTypes = Gson().fromJson<List<VehicleType>>(jObj.getJSONArray("data").toString(), object : TypeToken<List<VehicleType?>?>() {}.type)
                            setVehicleTypes()
                            // if user is already logedin then we will save the user data and go to the enable location screen
                        } else {
                            // if user is first time login then we will get the usser picture and name
                            CommonUtils.showAlert(this@EditDriverProfileActivity, 0, "", jObj.getString("message"))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext,
                                e.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                }
                mRegisterTask = null
            }
        }
        mRegisterTask!!.execute(null, null, null)
    }

    private fun setVehicleTypes() {
        val list: MutableList<String> = ArrayList()
        for (i in listVehicleTypes.indices) {
            var name = listVehicleTypes[i]!!.name
            if (listVehicleTypes[i]!!.type != "") {
                name += " - " + listVehicleTypes[i]!!.type
            }
            list.add(name)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVehicleType.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDriverDetails() {
        val param: MutableMap<String, String> = HashMap()
        param["mobile"] = session!!.getStringValue("mobile")

        val header: MutableMap<String, String> = HashMap()
        header["Authorization"] = session!!.getStringValue("auth_token")
        Log.e("TAG", "getDriverDetails: " + session!!.getStringValue("auth_token"))
        mRegisterTask = object : AsyncTask<Void, Void, String>() {
            var startTime = System.currentTimeMillis()

            override fun onPreExecute() {
                super.onPreExecute()
                pDialog!!.setMessage("Fetching User Details")
                showDialog()
            }

            override fun doInBackground(vararg params: Void?): String? {
                startTime = System.currentTimeMillis()
                return ServerUtilities.getServerResponse(applicationContext, ModuleConfig.URL_GET_USER, param, header)
            }

            override fun onPostExecute(result: String?) {
                hideDialog()
                if (result != null) {
                    try {
                        val jObj = JSONObject(result)
                        Log.d("TAG", "User Detail Response: $result")
                        if (jObj.optBoolean("status")) {
                            driver = Gson().fromJson(jObj.getJSONObject("data").toString(), Driver::class.java)
                            vehicle = Gson().fromJson(jObj.getJSONObject("vehicle").toString(), Vehicle::class.java)
                            session!!.setValue(AppConfig.CURRENT_USER, jObj.getJSONObject("data").toString())
                            session!!.setValue(AppConfig.USER_VEHICLE, jObj.getJSONObject("vehicle").toString())
                            session!!.setValue("image", driver.image)
                            session!!.setValue("mobile", driver.mobile)
                            session!!.setValue("name", driver.name)
                            session!!.setValue("dob", driver.dob)
                            session!!.setValue("gender", driver.gender)
                            session!!.setValue("status", driver.status)

                            profileImage = getByteArrayFromImageURL(driver.image)
                            drivingLicenseImage = getByteArrayFromImageURL(vehicle.driving_license)
                            rcImage = getByteArrayFromImageURL(vehicle.registration_card)
                            insuranceImage = getByteArrayFromImageURL(vehicle.insurance)
                            policeImage = getByteArrayFromImageURL(vehicle.police)

                            setUserDetails(driver, vehicle)
                        } else {
                            Toast.makeText(applicationContext, "Failed to load Profile", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Failed to load Profile", Toast.LENGTH_LONG).show()
                }
                mRegisterTask = null
            }
        }
        mRegisterTask!!.execute(null, null, null)
    }

    private fun getByteArrayFromImageURL(url: String): String {
        try {
            val imageUrl = URL(url)
            val ucon: URLConnection = imageUrl.openConnection()
            val `is`: InputStream = ucon.getInputStream()
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var read = 0
            while (`is`.read(buffer, 0, buffer.size).also { read = it } != -1) {
                baos.write(buffer, 0, read)
            }
            baos.flush()
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return ""
    }

    private fun setUserDetails(driver: Driver, vehicle: Vehicle) {
        Glide.with(this@EditDriverProfileActivity).load(AppConfig.URL_MAIN + "uploads/drivers/" + driver.image).into(ivProfile)
        btnBrowse.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                PermissionManager(this, this).getCameraMediaPermissions()
            } else {
                val intent = Intent(this, SelectPhotoActivity::class.java)
                startActivityForResult(intent, AppConfig.SELECT_PHOTO)
            }
        }
        inputName.setText(driver.name)
        tvDOB.text = driver.dob
        tvDOB.setOnClickListener {
            setDateTime(tvDOB)
        }
        val listGender: List<String> = ArrayList(listOf("Male", "Female", "Other"))
        spinnerGender.setSelection(listGender.indexOf(CommonUtils.capitalize(driver.gender)) + 1)

        for (vehicleType in listVehicleTypes) {
            if (vehicleType!!.id == vehicle.vehicle_type) {
                Log.d("TAG", "vehicle index: " + listVehicleTypes.indexOf(vehicleType))
                spinnerVehicleType.setSelection(listVehicleTypes.indexOf(vehicleType) + 1)
            }
        }
        inputModel.setText(vehicle.vehicle_model)
        inputRegistrationNumber.setText(vehicle.vehicle_number)
        inputExpiry.setText(vehicle.expiry_date)
        Glide.with(this@EditDriverProfileActivity).load(AppConfig.URL_MAIN + "uploads/drivers_docs/" + vehicle.driving_license).into(imgDrivingLicense)
        imgDrivingLicense.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                PermissionManager(this, this).getCameraMediaPermissions()
            } else {
                val intent = Intent(this, SelectPhotoActivity::class.java)
                startActivityForResult(intent, AppConfig.SELECT_DRIVING_LICENSE)
            }
        }
        Glide.with(this@EditDriverProfileActivity).load(AppConfig.URL_MAIN + "uploads/drivers_docs/" + vehicle.registration_card).into(imgRegistrationCard)
        imgRegistrationCard.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                PermissionManager(this, this).getCameraMediaPermissions()
            } else {
                val intent = Intent(this, SelectPhotoActivity::class.java)
                startActivityForResult(intent, AppConfig.SELECT_RC)
            }
        }
        Glide.with(this@EditDriverProfileActivity).load(AppConfig.URL_MAIN + "uploads/drivers_docs/" + vehicle.insurance).into(imgInsurance)
        imgInsurance.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                PermissionManager(this, this).getCameraMediaPermissions()
            } else {
                val intent = Intent(this, SelectPhotoActivity::class.java)
                startActivityForResult(intent, AppConfig.SELECT_INSURANCE)
            }
        }
        Glide.with(this@EditDriverProfileActivity).load(AppConfig.URL_MAIN + "uploads/drivers_docs/" + vehicle.police).into(imgPolice)
        imgPolice.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                PermissionManager(this, this).getCameraMediaPermissions()
            } else {
                val intent = Intent(this, SelectPhotoActivity::class.java)
                startActivityForResult(intent, AppConfig.SELECT_POLICE)
            }
        }
        btnSubmit.setOnClickListener {
            updateDriver()
        }
    }

    private fun setDateTime(textView: AppCompatTextView) {
        var mYear = 0
        var mMonth: Int = 0
        var mDay: Int = 0
        // Get Current Date
        if (mYear == 0) {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
        }
        val datePickerDialog = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
            val strDate: String = mYear.toString() + "-" + (mMonth + 1) + "-" + mDay
            val format = SimpleDateFormat("yyyy-MM-dd")
            try {
                val date = format.parse(strDate)
                val format2 = SimpleDateFormat("yyyy-MM-dd")
                textView.text = format2.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private fun updateDriver() {
        pDialog!!.setMessage("Saving...")
        showDialog()
        val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", driver.id.toString())
                .addFormDataPart("name", inputName.text.toString())
                .addFormDataPart("mobile", driver.mobile)
                .addFormDataPart("dob", tvDOB.text.toString())
                .addFormDataPart("gender", spinnerGender.selectedItem.toString())
                .addFormDataPart("fb_id", driver.fb_id)
                .addFormDataPart("token", driver.token)
                .addFormDataPart("vehicle_type", listVehicleTypes[spinnerVehicleType.selectedItemPosition - 1]!!.id.toString())
                .addFormDataPart("vehicle_model", inputModel.text.toString())
                .addFormDataPart("vehicle_number", inputRegistrationNumber.text.toString())
                .addFormDataPart("expiry_date", inputExpiry.text.toString())
                .addFormDataPart("image", profileImage)
                .addFormDataPart("driving_license", drivingLicenseImage)
                .addFormDataPart("registration_card", rcImage)
                .addFormDataPart("insurance", insuranceImage)
                .addFormDataPart("police_verification", policeImage)
                .build()
        apiService!!.updateDriver(requestBody).enqueue(object : Callback<JSONObject?> {
            override fun onResponse(call: Call<JSONObject?>, response: Response<JSONObject?>) {
                if (response.isSuccessful) {
                    Log.e("TAG", "onResponse: $response")
                    Toast.makeText(this@EditDriverProfileActivity, "User updated Successfully", Toast.LENGTH_SHORT).show()
                    getDriverDetails()
                } else {
                    Toast.makeText(this@EditDriverProfileActivity, "Failed to Update User", Toast.LENGTH_SHORT).show()
                }
                hideDialog()
            }

            override fun onFailure(call: Call<JSONObject?>, t: Throwable) {
                Toast.makeText(this@EditDriverProfileActivity, "Failed to Update User", Toast.LENGTH_SHORT).show()
                hideDialog()
            }
        })
    }

    private fun showDialog() {
        if (!pDialog!!.isShowing) pDialog!!.show()
    }

    private fun hideDialog() {
        if (pDialog!!.isShowing) pDialog!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                AppConfig.SELECT_PHOTO -> if (data!!.hasExtra("image")) {
                    val byteArray = data.getByteArrayExtra("image")
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    profileImage = CommonUtils.bitmapToString(bitmap)
                    ivProfile.setImageBitmap(bitmap)
                }
                AppConfig.SELECT_DRIVING_LICENSE -> if (data!!.hasExtra("image")) {
                    val byteArray = data.getByteArrayExtra("image")
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    drivingLicenseImage = CommonUtils.bitmapToString(bitmap)
                    imgDrivingLicense.setImageBitmap(bitmap)
                }
                AppConfig.SELECT_RC -> if (data!!.hasExtra("image")) {
                    val byteArray = data.getByteArrayExtra("image")
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    rcImage = CommonUtils.bitmapToString(bitmap)
                    imgRegistrationCard.setImageBitmap(bitmap)
                }
                AppConfig.SELECT_INSURANCE -> if (data!!.hasExtra("image")) {
                    val byteArray = data.getByteArrayExtra("image")
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    insuranceImage = CommonUtils.bitmapToString(bitmap)
                    imgInsurance.setImageBitmap(bitmap)
                }
                AppConfig.SELECT_POLICE -> if (data!!.hasExtra("image")) {
                    val byteArray = data.getByteArrayExtra("image")
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    policeImage = CommonUtils.bitmapToString(bitmap)
                    imgPolice.setImageBitmap(bitmap)
                }
            }
        }
    }
}