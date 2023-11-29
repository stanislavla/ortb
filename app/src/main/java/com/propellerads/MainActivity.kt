package com.propellerads

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonParser

import org.apache.commons.codec.binary.Hex
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    private lateinit var nextOrtbRequest: Button
    private val zoneId = 6105005
    private val baseUrl = "http://offers.propellerads.com/api/v1/ads_open_rtb/$zoneId/"
    private val auth = generateAuthByZone(zoneId)
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(ORTBApiService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nextOrtbRequest = findViewById(R.id.nextOrtbRequestButton)
        nextOrtbRequest.setOnClickListener { sendOrtb() }
    }

    //TODO: clear current web view
    private fun clearWebView(webView: WebView) {
        val emptyHtml = "<html><head></head><body></body></html>"
        webView.loadData(emptyHtml, "text/html", "UTF-8")
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearView()
        webView.loadUrl("about:blank");
        webView.reload();
    }

    private fun sendOrtb() {
        val url = "$baseUrl?auth=$auth"
        val webView: WebView = findViewById(R.id.webview)
        val requestBody = JsonParser().parse(getOrtbRequest()).asJsonObject
        Log.i(
            MainActivity::class.toString(),
            String.format("url: %s\nrequest body: %s", url, requestBody)
        )
        val call = apiService.sendOfferApiOrtb(url, requestBody)

        call.enqueue(object : retrofit2.Callback<ResponseData> {
            @SuppressLint("SetJavaScriptEnabled")
            override fun onResponse(
                call: Call<ResponseData>,
                response: retrofit2.Response<ResponseData>
            ) {

                if (response.isSuccessful) {

                    val responseData = response.body()

                    if (responseData != null) {
                        Log.i(MainActivity::class.toString(), responseData.toString())
                        val unencodedAdm = response.body()!!.seatbid?.get(0)?.bid?.get(0)?.adm
                        webView.settings.javaScriptEnabled = true
                        webView.settings.javaScriptCanOpenWindowsAutomatically = true
                        webView.getSettings().setDomStorageEnabled(true);
                        val encodedHtml =
                            Base64.encodeToString(unencodedAdm?.toByteArray(), Base64.NO_PADDING)
                        webView.loadData(encodedHtml, "text/html", "base64")
                    } else {
                        Log.i(MainActivity::class.toString(), "Response is empty")
                    }
                } else {
                    Log.i(
                        MainActivity::class.toString(),
                        String.format(
                            "Response error: code %s message: %s",
                            response.code(),
                            response.message()
                        )
                    )
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Log.i(MainActivity::class.toString(), "Request failed!")
            }
        })
    }

    private fun getOrtbRequest(): String {
        val context = applicationContext
        val resourceId = R.raw.ortb_request
        val inputStream = context.resources.openRawResource(resourceId)
        val bufferedReader = inputStream.bufferedReader()
        return bufferedReader.use { it.readText() }
    }

    private fun generateAuthByZone(zoneId: Int): String? {
        return try {
            val crypt = MessageDigest.getInstance("SHA-1")
            crypt.reset()
            crypt.update((zoneId.toString() + "derek_parker").toByteArray(UTF_8))
            Hex.encodeHexString(crypt.digest())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e.message)
        }
    }

    // For intent testing
    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebViewCheckIntent() {
        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        myWebView.getSettings().setDomStorageEnabled(true);
        val unencodedAdm =
            """
                <!doctype html>
                <html lang="en" xmlns="http://www.w3.org/1999/html">
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta charset="UTF-8"/>
                    <meta http-equiv="cache-control" content="no-cache"/>
                    <meta http-equiv="expires" content="0"/>
                    <meta http-equiv="pragma" content="no-cache"/>
                </head>
                <body style="margin: 0;">
                
                <a href="intent://_ccAXpgT0DA/#Intent;scheme=vnd.youtube;package=com.google.android.youtube;S.browser_fallback_url=market://details?id=com.google.android.youtube;end;">youtube or market</a>
                
                </body>
                </html>         
            """
        // 1 try through encoded html
        val encodedHtml = Base64.encodeToString(unencodedAdm.toByteArray(), Base64.NO_PADDING)
        myWebView.loadData(encodedHtml, "text/html", "base64")

        // 2 or try through opening URL
        myWebView.loadUrl("https://stanislavla.github.io/ortb/youtube")
    }
}
