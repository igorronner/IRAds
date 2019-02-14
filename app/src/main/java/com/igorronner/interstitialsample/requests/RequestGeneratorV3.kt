package com.igorronner.interstitialsample.requests

import com.google.gson.GsonBuilder
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.security.KeyStore
import java.util.concurrent.TimeUnit


object RequestGeneratorV3 {

    private var baseURL = "https://mobillssincv3homolog.azurewebsites.net"
    private var persistent: Boolean = false
    private var ssl: Boolean = false
    private var headers = HashMap<String, Any>()
    private var readTimeOut = 0
    private var writeTimeOut = 0
    private var connectTimeOut = 0


    fun isPersistent(): Boolean {
        return persistent
    }

    fun setPersistent(persistent: Boolean): RequestGeneratorV3 {
        this.persistent = persistent

        return this
    }


    fun setBaseURL(baseURL: String): RequestGeneratorV3 {
        this.baseURL = baseURL
        return this
    }


    fun isSsl(): Boolean {
        return ssl
    }

    fun setSsl(ssl: Boolean): RequestGeneratorV3 {
        this.ssl = ssl
        return this
    }

    fun setReadTimeOut(timeout:Int):RequestGeneratorV3{
        this.readTimeOut = timeout
        return this
    }

    fun setWriteTimeOut(timeout: Int):RequestGeneratorV3{
        this.writeTimeOut = writeTimeOut
        return this
    }

    fun setConnectTimeOut(timeout: Int):RequestGeneratorV3{
        this.connectTimeOut = timeout
        return this
    }

    fun getHeaders(): Map<String, Any>? {
        return headers
    }

    fun setHeaders(headers: HashMap<String, Any>): RequestGeneratorV3 {
        this.headers = headers
        return this
    }

    fun setToken(token: String?) :RequestGeneratorV3{
        val params = HashMap<String, Any>()
        params["authorization"] = "bearer $token"
        params["Accept-Encoding"] = ""

        return this.setHeaders(params)
    }

    fun <S> createService(serviceClass: Class<S>): S {

        val httpClientBuilder = OkHttpClient.Builder()

        try {
            if (ssl) {
                try {
                    val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
                    trustStore.load(null, null)
                    httpClientBuilder.hostnameVerifier(NullHostNameVerifier())
                    httpClientBuilder.sslSocketFactory(Security(trustStore), MyTrustManager())
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }

            }


        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        if(readTimeOut>0)
            httpClientBuilder.readTimeout(readTimeOut.toLong(), TimeUnit.SECONDS)

        if(writeTimeOut>0)
            httpClientBuilder.writeTimeout(writeTimeOut.toLong(), TimeUnit.SECONDS)

        if (connectTimeOut>0)
            httpClientBuilder.connectTimeout(connectTimeOut.toLong(), TimeUnit.SECONDS)

        if (persistent) {
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            CookieHandler.setDefault(cookieManager)
            httpClientBuilder.cookieJar(JavaNetCookieJar(cookieManager))
        }

        val gson = GsonBuilder()
                            .setLenient()
                            .create()

        val retrofitBuilder = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))

        httpClientBuilder.addInterceptor { chain ->

            var request = chain.request()
            val builder = request.newBuilder()
            for ((key, value) in headers) {
                builder.addHeader(key, value.toString())
            }
            request = builder.build()
            chain.proceed(request)
        }

        retrofitBuilder.client(httpClientBuilder.build())
        return retrofitBuilder.build().create(serviceClass)
    }



}
