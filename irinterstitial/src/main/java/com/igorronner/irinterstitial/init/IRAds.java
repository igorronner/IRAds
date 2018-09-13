package com.igorronner.irinterstitial.init;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.igorronner.irinterstitial.R;
import com.igorronner.irinterstitial.dto.RemoteConfigDTO;
import com.igorronner.irinterstitial.preferences.MainPreference;
import com.igorronner.irinterstitial.services.IRInterstitialService;
import com.igorronner.irinterstitial.services.ManagerNativeAd;
import com.igorronner.irinterstitial.services.RemoteConfigService;
import com.igorronner.irinterstitial.views.SplashActivity;


public class IRAds {

    public static IRAds.Builder startInit(Context context) {
        return new IRAds.Builder();
    }

    private IRAds(final IRAds.Builder builder) {
        ConfigUtil.LOGO = builder.logo;
        ConfigUtil.INTERSTITIAL_ID = builder.addUnitId;
        ConfigUtil.NATIVE_AD_ID = builder.nativeAdId;
        ConfigUtil.PUBLISHER_INTERSTITIAL_ID = builder.nativeAdIdV2;
        ConfigUtil.PRODUCT_SKU = builder.productSku;
        ConfigUtil.APP_PREFIX = builder.appPrefix;
    }

    public static class Builder {
        @DrawableRes
        private int logo;
        private IRAds IRAds;
        private String addUnitId;
        private String nativeAdId;
        private String nativeAdIdV2;
        private String productSku;
        // Prefix for concat keys on remote config...
        private String appPrefix;

        public Builder() {
        }

        public Builder setLogo(@DrawableRes int logo) {
            this.logo = logo;
            return this;
        }

        public Builder setAddUnitId(String addUnitId) {
            this.addUnitId = addUnitId;
            return this;
        }

        public Builder setNativeAdId(String nativeAdId) {
            this.nativeAdId = nativeAdId;
            return this;
        }

        public Builder setNativeAdIdV2(String nativeAdIdV2) {
            this.nativeAdIdV2 = nativeAdIdV2;
            return this;
        }

        public Builder enablePurchace(String productSku){
            this.productSku = productSku;
            return this;
        }

        public Builder setAppPrefix(String appPrefix){
            this.appPrefix = appPrefix;
            return this;
        }

        public IRAds build() {
            this.IRAds = new IRAds(this);

            return this.IRAds;
        }

    }

    public static void showInterstitial(final Activity activity, final RemoteConfigDTO remoteConfigDTO, final String titleDialog){
        showInterstitial(activity, remoteConfigDTO, titleDialog, false);
    }

    public static void showInterstitial(final Activity activity, final RemoteConfigDTO remoteConfigDTO, final String titleDialog, final boolean finishAll){
        loadRemoteConfig(activity, new RemoteConfigService.ServiceListener<RemoteConfigDTO>() {
            @Override
            public void onComplete(RemoteConfigDTO result) {
                new IRInterstitialService(activity, remoteConfigDTO).showInterstitial(titleDialog, finishAll);
            }
        });
    }

    public static void showInterstitial(final Activity activity, final RemoteConfigDTO remoteConfigDTO){
        showInterstitial(activity,remoteConfigDTO, null, false);
    }

    public static void showInterstitialBeforeIntent(final Activity activity, final RemoteConfigDTO remoteConfigDTO, final Intent intent, final boolean finishAll, final String titleDialog){
        new IRInterstitialService(activity, remoteConfigDTO).showInterstitialBeforeIntent(activity, intent, titleDialog);
    }
    public static void showInterstitialBeforeIntent(final Activity activity, final RemoteConfigDTO remoteConfigDTO, final Intent intent, final String titleDialog){
        showInterstitialBeforeIntent(activity, remoteConfigDTO, intent, false, titleDialog);
    }

    public static void openSplashScreen(final Activity activity){
        if (!isPremium(activity))
            activity.startActivity(new Intent(activity, SplashActivity.class));
    }

    public static void showInterstitalOnFinish(final Activity activity){
        loadRemoteConfig(activity, new RemoteConfigService.ServiceListener<RemoteConfigDTO>() {
            @Override
            public void onComplete(RemoteConfigDTO result) {
                if (result.getFinishWithInterstitial())
                    showInterstitial(activity, result, activity.getString(R.string.going_out));
                else
                    ActivityCompat.finishAffinity(activity);
            }
        });
    }

    public static void loadRemoteConfig(Activity activity, RemoteConfigService.ServiceListener<RemoteConfigDTO> serviceListener ){
        RemoteConfigService.getInstance(activity).loadRemoteConfig(serviceListener);
    }

    public static void loadCardAdView(Activity activity, View cardView,  NativeAppInstallAdView nativeAppInstallAdView){
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
}