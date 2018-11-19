package com.igorronner.irinterstitial.init;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.igorronner.irinterstitial.R;
import com.igorronner.irinterstitial.dto.RemoteConfigDTO;
import com.igorronner.irinterstitial.preferences.MainPreference;
import com.igorronner.irinterstitial.services.IRInterstitialService;
import com.igorronner.irinterstitial.services.ManagerNativeAd;
import com.igorronner.irinterstitial.services.RemoteConfigService;
import com.igorronner.irinterstitial.views.SplashActivity;

public class IRAds implements RemoteConfigService.ServiceListener<RemoteConfigDTO>{

    public int state = 0;
    private static final int STOPPED = 910;
    private static final int RESUMED = 967;
    private Activity activity;
    private RemoteConfigDTO remoteConfigDTO;


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static IRAds newInstance(Activity activity){
        IRAds irAds = new IRAds();
        irAds.setActivity(activity);
        irAds.loadRemoteConfig(irAds);
        return irAds;
    }

    @Override
    public void onComplete(RemoteConfigDTO result) {
        remoteConfigDTO = result;
    }

    public void showInterstitial(){
        showInterstitial(false);
    }

    public void showInterstitial(final boolean finishAll) {
        if (remoteConfigDTO != null){
            new IRInterstitialService(IRAds.this, remoteConfigDTO).showInterstitial(finishAll);
            return;
        }

        loadRemoteConfig(new RemoteConfigService.ServiceListener<RemoteConfigDTO>() {
            @Override
            public void onComplete(RemoteConfigDTO result) {
                remoteConfigDTO = result;
                new IRInterstitialService(IRAds.this, result).showInterstitial( finishAll);
            }
        });
    }

    public void showInterstitialBeforeIntent(final Intent intent, final boolean finishAll){
        if (remoteConfigDTO != null){
            new IRInterstitialService(IRAds.this, remoteConfigDTO).showInterstitialBeforeIntent(intent, finishAll);
            return;
        }
        loadRemoteConfig(new RemoteConfigService.ServiceListener<RemoteConfigDTO>() {
            @Override
            public void onComplete(RemoteConfigDTO result) {
                remoteConfigDTO = result;
                new IRInterstitialService(IRAds.this, result).showInterstitialBeforeIntent(intent, finishAll);
            }
        });


    }
    public void showInterstitialBeforeIntent(final Intent intent){
        showInterstitialBeforeIntent(intent, false);
    }


    public void showInterstitialBeforeFragment(final Fragment fragment, final @IdRes int containerViewId,
                                               final FragmentActivity fragmentActivity){

        if (remoteConfigDTO != null){
            new IRInterstitialService(IRAds.this, remoteConfigDTO).showInterstitialBeforeFragment(fragment,  containerViewId, fragmentActivity);
            return;
        }

        loadRemoteConfig(new RemoteConfigService.ServiceListener<RemoteConfigDTO>() {
            @Override
            public void onComplete(RemoteConfigDTO result) {
                remoteConfigDTO = result;
                new IRInterstitialService(IRAds.this, result).showInterstitialBeforeFragment(fragment,  containerViewId, fragmentActivity);
            }
        });
    }

    public void openSplashScreen(){
        if (!isPremium(activity))
            activity.startActivity(new Intent(activity, SplashActivity.class));

    }

    public void showInterstitialOnFinish(){
        if (remoteConfigDTO != null){
            if (remoteConfigDTO.getFinishWithInterstitial())
                showInterstitial(remoteConfigDTO);
            else
                ActivityCompat.finishAffinity(activity);
            return;
        }
        loadRemoteConfig(new RemoteConfigService.ServiceListener<RemoteConfigDTO>() {
            @Override
            public void onComplete(RemoteConfigDTO result) {
                remoteConfigDTO = result;
                if (result.getFinishWithInterstitial())
                    showInterstitial(result);
                else
                    ActivityCompat.finishAffinity(activity);
            }
        });
    }

    public void showInterstitial(RemoteConfigDTO result) {
        new IRInterstitialService(IRAds.this, result).showInterstitial( false);
    }

    public void loadRemoteConfig(RemoteConfigService.ServiceListener<RemoteConfigDTO> serviceListener ){
        RemoteConfigService.getInstance(activity).loadRemoteConfig(serviceListener);
    }

    public static void loadCardAdView(Activity activity, View cardView, NativeAppInstallAdView nativeAppInstallAdView){
        ManagerNativeAd.getInstance(activity)
                .setAdmobAdUnitId(ConfigUtil.NATIVE_AD_ID)
                .setShowProgress(false)
                .loadAppInstallAdView(cardView, nativeAppInstallAdView);
    }

    public static void loadNativeAd(Activity activity, boolean showProgress){
        ManagerNativeAd.getInstance(activity)
                .setAdmobAdUnitId(ConfigUtil.NATIVE_AD_ID)
                .setShowProgress(showProgress)
                .loadAppInstallAdView((NativeAppInstallAdView) activity.findViewById(R.id.adViewNative));
    }

    public static void loadNativeAd(Activity activity, boolean showProgress, NativeAppInstallAdView nativeAppInstallAdView){
        ManagerNativeAd.getInstance(activity)
                .setAdmobAdUnitId(ConfigUtil.NATIVE_AD_ID)
                .setShowProgress(showProgress)
                .loadAppInstallAdView(nativeAppInstallAdView);
    }

    public static boolean isPremium(Context context){
        return MainPreference.isPremium(context);
    }

    public void onStop(){
        state = STOPPED;
    }

    public void onResume(){
        state = RESUMED;

    }

    public boolean isStopped(){
        return state == STOPPED;
    }

    public boolean isResumed(){
        return state == RESUMED;
    }
}
