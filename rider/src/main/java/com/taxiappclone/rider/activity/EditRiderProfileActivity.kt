package com.taxiappclone.rider.activity

import android.Manifest
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.util.Base64.encodeToString
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.rengwuxian.materialedittext.MaterialEditText
import com.taxiappclone.common.activity.SelectPhotoActivity
import com.taxiappclone.common.app.AppConfig
import com.taxiappclone.common.app.CommonUtils
import com.taxiappclone.common.helper.SessionManager
import com.taxiappclone.common.model.User
import com.taxiappclone.common.network.APIService
import com.taxiappclone.common.network.ApiUtils
import com.taxiappclone.common.utils.PermissionManager
import com.taxiappclone.common.utils.ServerUtilities
import com.taxiappclone.common.view.CircleImageView
import com.taxiappclone.rider.R
import com.taxiappclone.rider.app.AppController
import com.taxiappclone.rider.app.ModuleConfig
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

class EditRiderProfileActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var actionBar: ActionBar? = null
    private var mRegisterTask: AsyncTask<Void, Void, String>? = null

    private var pDialog: ProgressDialog? = null
    private var session: SessionManager? = null
    private lateinit var profileImage: String

    private lateinit var ivProfile: CircleImageView
    private lateinit var btnBrowse: AppCompatButton
    private lateinit var etName: MaterialEditText
    private lateinit var etEmail: MaterialEditText
    private lateinit var tvDOB: AppCompatTextView
    private lateinit var etMobile: MaterialEditText
    private lateinit var etAlternateMobile: MaterialEditText
    private lateinit var etAddress: MaterialEditText
    private lateinit var spinnerCountry: MaterialSpinner
    private lateinit var spinnerState: MaterialSpinner
    private lateinit var spinnerCity: MaterialSpinner
    private lateinit var btnSave: AppCompatButton

    lateinit var user: User
    var apiService: APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_rider_profile)

        ivProfile = findViewById(R.id.ivProfile)
        btnBrowse = findViewById(R.id.btnBrowse)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        tvDOB = findViewById(R.id.tvDOB)
        etMobile = findViewById(R.id.etMobile)
        etAlternateMobile = findViewById(R.id.etAlternateMobile)
        etAddress = findViewById(R.id.etAddress)
        spinnerCountry = findViewById(R.id.spinnerCountry)
        spinnerState = findViewById(R.id.spinnerState)
        spinnerCity = findViewById(R.id.spinnerCity)
        btnSave = findViewById(R.id.btnSave)

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

        getUserDetails()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserDetails() {
        val param: MutableMap<String, String> = HashMap()
        param["mobile"] = session!!.getStringValue("mobile")
        val header: MutableMap<String, String> = HashMap()
        header["Authorization"] = session!!.getStringValue("auth_token")
        Log.e("TAG", "getUserDetails: " + session!!.getStringValue("auth_token"))
        mRegisterTask = object : AsyncTask<Void, Void, String>() {
            var startTime = System.currentTimeMillis()

            override fun onPreExecute() {
                super.onPreExecute()
                pDialog!!.setMessage("Fetching User Details")
                showDialog()
            }

            override fun doInBackground(vararg p0: Void?): String? {
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
                            val userData = jObj.getJSONObject("data").toString()
                            session!!.setValue(AppConfig.USER, userData)
                            user = AppController.getInstance().user
                            session!!.setValue("image", jObj.getJSONObject("data").getString("image"))
                            session!!.setValue("mobile", user.mobile)
                            session!!.setValue("name", user.name)
                            session!!.setValue("dob", user.dob)

                            profileImage = getByteArrayFromImageURL(user.image)

                            setUserDetails(user)
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
            return encodeToString(baos.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return ""
    }

    private fun setUserDetails(user: User) {
        Glide.with(this@EditRiderProfileActivity).load(AppConfig.URL_MAIN + "uploads/users/" + user.image).into(ivProfile)
        btnBrowse.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                PermissionManager(this, this).getCameraMediaPermissions()
            } else {
                val intent = Intent(this, SelectPhotoActivity::class.java)
                startActivityForResult(intent, AppConfig.SELECT_PHOTO)
            }
        }
        etName.setText(user.name)
        etEmail.setText(user.email)
        tvDOB.text = user.dob
        tvDOB.setOnClickListener {
            setDateTime(tvDOB)
        }
        etMobile.setText(user.mobile)
        etAlternateMobile.setText(user.mobile_alt)
        etAddress.setText(user.address)
        btnSave.setOnClickListener {
            updateUser()
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
                val format2 = SimpleDateFormat("dd-MM-yyyy")
                textView.text = format2.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private fun updateUser() {
        pDialog!!.setMessage("Saving...")
        showDialog()
        user.name = etName.text.toString()
        user.dob = tvDOB.text.toString()
        user.mobile = etMobile.text.toString()
        user.mobile_alt = etAlternateMobile.text.toString()
        user.address = etAddress.text.toString()
        user.email = etEmail.text.toString()
        val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", user.id)
                .addFormDataPart("name", etName.text.toString())
                .addFormDataPart("dob", tvDOB.text.toString())
                .addFormDataPart("mobile", etMobile.text.toString())
                .addFormDataPart("mobile_alt", etAlternateMobile.text.toString())
                .addFormDataPart("address", etAddress.text.toString())
                .addFormDataPart("country_id", 1.toString())
                .addFormDataPart("state_id", 1.toString())
                .addFormDataPart("city_id", 1.toString())
                .addFormDataPart("email", etEmail.text.toString())
                .addFormDataPart("image", profileImage)
                .build()
        apiService!!.updateRider(requestBody).enqueue(object : Callback<JSONObject?> {
            override fun onResponse(call: Call<JSONObject?>, response: Response<JSONObject?>) {
                if (response.isSuccessful) {
                    Log.e("TAG", "onResponse: $response")
                    Toast.makeText(this@EditRiderProfileActivity, "User updated Successfully", Toast.LENGTH_SHORT).show()
                    getUserDetails()
                } else {
                    Toast.makeText(this@EditRiderProfileActivity, "Failed to Update User", Toast.LENGTH_SHORT).show()
                }
                hideDialog()
            }

            override fun onFailure(call: Call<JSONObject?>, t: Throwable) {
                Toast.makeText(this@EditRiderProfileActivity, "Failed to Update User", Toast.LENGTH_SHORT).show()
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
        if (resultCode == RESULT_OK && requestCode == AppConfig.SELECT_PHOTO && data!!.hasExtra("image")) {
            val byteArray = data.getByteArrayExtra("image")
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            ivProfile.setImageBitmap(bitmap)
            profileImage = CommonUtils.bitmapToString(bitmap)
        }
    }
}