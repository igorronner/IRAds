package com.igorronner.irinterstitial.init;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;

import com.google.android.gms.ads.formats.UnifiedNativeAdView;
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
    private ManagerNativeAd managerNativeAd;


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
        ManagerNativeAd managerNativeAd = new ManagerNativeAd(activity)
                .setAdmobAdUnitId(ConfigUtil.NATIVE_AD_ID)
                .setExpensiveAdmobAdUnitId(ConfigUtil.EXPENSIVE_NATIVE_AD_ID);
        irAds.setManagerNativeAd(managerNativeAd);
        return irAds;
    }

    @Override
    public void onComplete(RemoteConfigDTO result) {
        remoteConfigDTO = result;
    }


    private void setManagerNativeAd(ManagerNativeAd managerNativeAd) {
        this.managerNativeAd = managerNativeAd;
    }


    public void forceShowInterstitial(){
        forceShowInterstitial(true);
    }

    public void forceShowInterstitial(final Boolean finish){
        new IRInterstitialService(IRAds.this).forceShowInterstitial(finish);
    }

    public void showInterstitial(){
        showInterstitial(true);
    }

    public void showInterstitial(final boolean finish) {
        new IRInterstitialService(IRAds.this).showInterstitial(finish);
    }

    public void forceShowInterstitialBeforeIntent(final Intent intent){
        forceShowInterstitialBeforeIntent(intent, false);
    }

    public void forceShowInterstitialBeforeIntent(final Intent intent, final boolean finishAll){
        new IRInterstitialService(IRAds.this).forceShowInterstitialBeforeIntent(intent, finishAll);
    }

    public void showInterstitialBeforeIntent(final Intent intent, final boolean finishAll){
        new IRInterstitialService(IRAds.this).showInterstitialBeforeIntent(intent, finishAll);
    }


    public void showInterstitialBeforeIntent(final Intent intent){
        showInterstitialBeforeIntent(intent, false);
    }

    public void forceShowInterstitialBeforeIntent(final Fragment fragment, final @IdRes int containerViewId,
                                                  final FragmentActivity fragmentActivity){
        new IRInterstitialService(IRAds.this).forceShowInterstitialBeforeFragment(fragment,  containerViewId, fragmentActivity);
    }

    public void showInterstitialBeforeFragment(final Fragment fragment, final @IdRes int containerViewId,
                                               final FragmentActivity fragmentActivity){
        new IRInterstitialService(IRAds.this).showInterstitialBeforeFragment(fragment,  containerViewId, fragmentActivity);

    }

    public void openSplashScreen(){
        if (!isPremium(activity))
            activity.startActivity(new Intent(activity, SplashActivity.class));

    }

    @Deprecated
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

    @Deprecated
    public void showInterstitial(RemoteConfigDTO result) {
        new IRInterstitialService(IRAds.this).showInterstitial();
    }

    public void loadRemoteConfig(RemoteConfigService.ServiceListener<RemoteConfigDTO> serviceListener ){
        RemoteConfigService.getInstance(activity).loadRemoteConfig(serviceListener);
    }

    public void loadNativeAd(boolean showProgress, UnifiedNativeAdView unifiedNativeAdView){
        managerNativeAd.setShowProgress(showProgress)
                .loadNativeAd(null, unifiedNativeAdView);
    }

    public void loadNativeAd(ViewGroup cardView, boolean showProgress, UnifiedNativeAdView unifiedNativeAdView){
        managerNativeAd.setShowProgress(showProgress)
                .loadNativeAd(cardView, unifiedNativeAdView);
    }

    public void loadNativeAd(ViewGroup cardView, UnifiedNativeAdView unifiedNativeAdView){
        managerNativeAd.setShowProgress(false)
                .loadNativeAd(cardView, unifiedNativeAdView);
    }

    public void loadNativeAd(){
        loadNativeAd(false, (UnifiedNativeAdView) activity.findViewById(R.id.adViewNative));
    }

    public void loadNativeAd(boolean showProgress){
        loadNativeAd(showProgress, (UnifiedNativeAdView) activity.findViewById(R.id.adViewNative));
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
