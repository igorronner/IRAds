package com.igorronner.irinterstitial.services;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.igorronner.irinterstitial.BuildConfig;
import com.igorronner.irinterstitial.dto.RemoteConfigDTO;
import com.igorronner.irinterstitial.init.ConfigUtil;
import com.igorronner.irinterstitial.preferences.MainPreference;
import com.igorronner.irinterstitial.utils.DateUtils;

import java.util.Calendar;

public class RemoteConfigService {

    private Activity context;
    private static RemoteConfigService instance;
    public FirebaseRemoteConfig mFirebaseRemoteConfig;
    private ServiceListener serviceListener;


    private static final String SHOW_SPLASH = ConfigUtil.APP_PREFIX+"show_splash";
    private static final String FINISH_WITH_INTERSTITIAL = ConfigUtil.APP_PREFIX+"finish_with_interstitial";
    private static final String AD_VERSION = ConfigUtil.APP_PREFIX+"ad_version";
    private static final String PUBLISHER_INTERSTITIAL_ID  = ConfigUtil.APP_PREFIX+"publisher_interstitial_id";
    private static final String INTERSTITIAL_ID  = ConfigUtil.APP_PREFIX+"interstitial_id";
    
    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


    public interface ServiceListener<T> {
        void onComplete(T result);
    }

    public RemoteConfigService.ServiceListener getServiceListener() {
        return serviceListener;
    }

    public RemoteConfigService setServiceListener(RemoteConfigService.ServiceListener serviceListener) {
        this.serviceListener = serviceListener;
        return this;
    }

    public RemoteConfigService() { }

    public static RemoteConfigService getInstance(Activity activity) {
        if (instance == null) {
            instance = new RemoteConfigService();
            FirebaseApp.initializeApp(activity);
            instance.mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings
                    .Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            instance.mFirebaseRemoteConfig.setConfigSettings(configSettings);
        }

        instance.setContext(activity);

        return instance;
    }

    public void loadRemoteConfig(final ServiceListener<RemoteConfigDTO> serviceListener){
        mFirebaseRemoteConfig.fetch(cacheExpiration())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        serviceListener.onComplete(new RemoteConfigDTO());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        RemoteConfigDTO remoteConfigDTO = new RemoteConfigDTO();
                        mFirebaseRemoteConfig.activateFetched();
                        remoteConfigDTO.setShowSplash( mFirebaseRemoteConfig.getBoolean(SHOW_SPLASH) && !MainPreference.isPremium(context));
                        remoteConfigDTO.setAdVersion(mFirebaseRemoteConfig.getLong(AD_VERSION));
                        remoteConfigDTO.setFinishWithInterstitial(mFirebaseRemoteConfig.getBoolean(FINISH_WITH_INTERSTITIAL));
                        remoteConfigDTO.setPublisherInterstitialId(mFirebaseRemoteConfig.getString(PUBLISHER_INTERSTITIAL_ID));
                        remoteConfigDTO.setInterstitialId(mFirebaseRemoteConfig.getString(INTERSTITIAL_ID));
                        serviceListener.onComplete(remoteConfigDTO);
                    }
                });

    }

    public void canShowSplash(final ServiceListener<Boolean> serviceListener){
        loadRemoteConfig(new ServiceListener<RemoteConfigDTO>() {
            @Override
            public void onComplete(RemoteConfigDTO result) {
                serviceListener.onComplete(result.getShowSplash());
            }
        });
    }

    public void canFinishWithInterstitial(final ServiceListener<Boolean> serviceListener){
        loadRemoteConfig(new ServiceListener<RemoteConfigDTO>() {
            @Override
            public void onComplete(RemoteConfigDTO result) {
                serviceListener.onComplete(result.getFinishWithInterstitial());
            }
        });
    }

    private long cacheExpiration(){
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        return cacheExpiration;

    }

}