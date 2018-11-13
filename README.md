# IRAds 


> Toolkit for load ads between change activity and fragment

Download
--------

Step 1 - Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Step 2 - Add the dependency

```groovy
dependencies {
  implementation 'com.github.igorronner:IRAds:1.1.2'
}
```

Firebase Remote Config
--------

**Parameters**:

> Put this parameters on remote config before start application

***publisher_interstitial_id***

***ad_version***
  > version = 1 -> InterstitialAd

  > version = 2 -> PublisherInterstitialAd

***finish_with_interstitial***

***show_splash***

The parameter is always prefix + key, but when you don't pass the prefix on configuration the parameter is just the key


Getting Started
--------

**Put the code bellow on Application Class**:

```java
IRAdsInit.start()
            //For test
            .setAppId("ca-app-pub-3940256099942544~3347511713")
            //.setAppId("YOUR_ADMOB_APP_ID")

            //For test
            .setInterstitialId("ca-app-pub-3940256099942544/1033173712")
            //.setInterstitialId("PLACE_YOUR_ADD_UNIT_ID")

            .setLogo(R.mipmap.ic_launcher)
            .setNativeAdId("PLACE_YOUR_NATIVE_AD_ID")
            .setAppPrefix("lib_")
            //For test
            //.setPublisherInterstitialId("/6499/example/interstitial")
            .setPublisherInterstitialId("PLACE_YOUR_PUBLISHER_AD_ID")

            .build(this)

```

**Splash Activity:**

> Put the code bellow on LAUNCHER Activity

```java
class MainActivity : AppCompatActivity() {

    private lateinit var adsInstance:IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adsInstance = IRAds.newInstance(this)
        adsInstance.openSplashScreen()
    }

    override fun onBackPressed() {
        adsInstance.showInterstitialOnFinish()
    }

    override fun onStop() {
        adsInstance.onStop()
        super.onStop()

    }

    override fun onResume() {
        adsInstance.onResume()
        super.onResume()
    }
}

```


**Show interstitial before Activity:**

> Remember use methods adsInstance.onStop() and  adsInstance.onResume() in the Lifecycle Activity

```java
  adsInstance.showInterstitialBeforeIntent(Intent(this, AnotherActivity::class.java))
```

**Show interstitial before Fragment:**

> Remember use methods adsInstance.onStop() and  adsInstance.onResume() in the Lifecycle Activity

```java
  adsInstance.showInterstitialBeforeFragment(Sample1Fragment(), R.id.frameLayout, this)
 
```

**Full example Show interstitial before Activity:**

```java
 
class SampleActivity : AppCompatActivity() {

    private lateinit var adsInstance:IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        adsInstance = IRAds.newInstance(this)

        button1.setOnClickListener {
            adsInstance.showInterstitialBeforeIntent(Intent(this, AnotherActivity::class.java))
        }
    }

    override fun onBackPressed() {
        adsInstance.showInterstitialOnFinish()
    }

    override fun onStop() {
        adsInstance.onStop()
        super.onStop()

    }

    override fun onResume() {
        adsInstance.onResume()
        super.onResume()
    }
}
```

**Full example Show interstitial before Fragment:**


```java
  
class FragmentSampleActivity : AppCompatActivity() {

    private lateinit var adsInstance: IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_sample)

        adsInstance = IRAds.newInstance(this)

        fragment1.setOnClickListener {
            adsInstance.showInterstitialBeforeFragment(Sample1Fragment(), R.id.frameLayout, this)
        }

        fragment2.setOnClickListener {
            adsInstance.showInterstitialBeforeFragment(Sample2Fragment(), R.id.frameLayout, this, getString(R.string.loading))
        }

    }

    override fun onStop() {
        super.onStop()
        adsInstance.onStop()
    }

    override fun onResume() {
        super.onResume()
        adsInstance.onResume()
    }

}

 
```

