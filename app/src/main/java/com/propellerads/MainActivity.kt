package com.propellerads

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.apache.commons.codec.binary.Hex
import org.json.JSONObject
import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


//прописать хост для оффер-апи
// 1 в url
// 2 в network_security_config

class MainActivity : AppCompatActivity() {

    private lateinit var nextOrtbRequest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nextOrtbRequest = findViewById(R.id.nextOrtbRequestButton)
        nextOrtbRequest.setOnClickListener { sendOfferApiOrtb() }
    }

    // For VW ORTB response testing
    private fun sendOfferApiOrtb() {
        val volleyQueue = Volley.newRequestQueue(this)
        val zoneId = 6148963
        val auth = generateAuthByZone(zoneId)
        val url = String.format(
            "http://offers.propellerads.com/api/v1/ads_open_rtb/6148963/?auth=%s",
            auth
        )
        val requestBody = JSONObject(
            """{ "id": "007e348ae16d9c913fdbd81ddf76b942", "imp": [ { "id": "1", "tagid": "fpznjnvorm", "bidfloor": 0.5282145818181817, "bidfloorcur": "USD", "clickbrowser": 0, "secure": 1, "exp": 3000, "rwdd": 0, "banner": { "w": 320, "h": 480, "pos": 0 } } ], "app": { "id": "e8512d49aed869b81cb8ff0026261e87", "name": "Word Wars - Word game", "cat": [ "IAB9" ], "bundle": "1245525123", "publisher": { "id": "515_11424" }, "storeurl": "https://apps.apple.com/app/id1245525899" }, "device": { "ext": { "ifv": "0C72E966-6EC7-407C-9360-F5E0BCC7483E", "atts": 0 }, "dnt": 1, "ua": "Mozilla/5.0 (iPhone; CPU iPhone OS 16_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16", "ip": "178.221.16.140", "geo": { "lat": 36.14059829711914, "lon": -86.75080108642578, "ipservice": 3, "country": "RS", "region": "TN", "metro": "659", "city": "Nashville", "zip": "37210", "type": 2 }, "carrier": "at&t internet", "language": "en", "make": "apple", "model": "iphone11,6", "os": "ios", "osv": "16.5.1", "js": 1, "connectiontype": 2, "devicetype": 4, "ifa": "00000000-0000-0000-0000-000000000000", "lmt": 1 }, "user": { "id": "707bb515-36b0-4a60-9861-43be8e000e62" }, "test": 0, "at": 2, "tmax": 285, "allimps": 0, "cur": [ "USD" ], "bcat": [ "IAB23", "IAB8-18", "IAB7-39", "IAB26", "IAB7-44", "IAB25", "IAB24", "IAB7-41", "IAB8-5", "IAB17-18", "IAB17-16", "IAB3-7", "IAB14-1", "IAB11-4", "IAB9-9" ], "badv": [ "my.com", "yandex.uz", "yandex.ru", "141lopaxb.com", "adquery.io", "yandex.com", "bigo.tv", "yandex.kz" ], "regs": { "coppa": 0, "ext": { "gdpr": 0 } } }"""
        )
        Log.i(
            MainActivity::class.toString(),
            String.format("url: %s\nrequest body: %s", url, requestBody)
        )

        val jsonObjectRequest = @SuppressLint("SetJavaScriptEnabled")
        object : JsonObjectRequest(
            Method.POST,
            url,
            requestBody,
            { response ->
                Log.i(MainActivity::class.toString(), response.toString())
                Toast.makeText(
                    this,
                    "Got request!",
                    Toast.LENGTH_LONG
                ).show()
                val myWebView: WebView = findViewById(R.id.webview)
                myWebView.settings.javaScriptEnabled = true
                myWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                myWebView.getSettings().setDomStorageEnabled(true);
                val unencodedAdm =
                    response.getJSONArray("seatbid").getJSONObject(0).getJSONArray("bid")
                        .getJSONObject(0).getString("adm");
                val encodedHtml =
                    Base64.encodeToString(unencodedAdm.toByteArray(), Base64.NO_PADDING)
                myWebView.loadData(encodedHtml, "text/html", "base64")
            },
            { error ->
                Toast.makeText(
                    this,
                    "Some error occurred!",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("MainActivity", "error: ${error.localizedMessage}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["X-Tracer"] = "llavskii123"
                return headers
            }
        }
        volleyQueue.add(jsonObjectRequest)
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
    private fun openVWcheckIntent() {
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

// 2 try through opening URL
        myWebView.loadUrl("https://stanislavla.github.io/ortb/youtube")
    }

}
