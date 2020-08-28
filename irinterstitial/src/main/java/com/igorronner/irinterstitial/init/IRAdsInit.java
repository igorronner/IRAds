package com.igorronner.irinterstitial.init;

import android.content.Context;
import androidx.annotation.DrawableRes;

import com.google.android.gms.ads.MobileAds;


public class IRAdsInit {

    // Builder
    public static IRAdsInit.Builder start() {
        return new IRAdsInit.Builder();
    }

    private IRAdsInit(final IRAdsInit.Builder builder) {
        ConfigUtil.LOGO = builder.logo;
        ConfigUtil.APP_ID = builder.appId;
        ConfigUtil.INTERSTITIAL_ID = builder.interstitialId;
        ConfigUtil.MID_INTERSTITIAL_ID = builder.midInterstitialId;
        ConfigUtil.EXPENSIVE_INTERSTITIAL_ID = builder.expensiveInterstitialId;
        ConfigUtil.NATIVE_AD_ID = builder.nativeAdId;
        ConfigUtil.MID_NATIVE_AD_ID = builder.midNativeAdId;
        ConfigUtil.EXPENSIVE_NATIVE_AD_ID = builder.expensiveNativeAdId;
        ConfigUtil.PRODUCT_SKU = builder.productSku;
        ConfigUtil.SUBSCRIPTION_SKU = builder.subscriptionSku;
        ConfigUtil.TESTER = builder.tester;
        ConfigUtil.AD_ENABLED = builder.adEnabled;
        ConfigUtil.ENABLE_CHECK_MOBILLS = builder.enableCheckMobills;
        ConfigUtil.BANNER_AD_ID = builder.bannerAdId;
        ConfigUtil.REWARDED_VIDEO_ID = builder.rewardedVideoId;

        if (builder.appPrefix != null) {
            ConfigUtil.APP_PREFIX = builder.appPrefix;
        }
    }

    public static class Builder {
        @DrawableRes
        private int logo;
        private IRAdsInit IRAdsInit;
        private String appId;
        private String interstitialId;
        private String midInterstitialId;
        private String expensiveInterstitialId;
        private String nativeAdId;
        private String midNativeAdId;
        private String expensiveNativeAdId;
        private String bannerAdId;
        private String rewardedVideoId;

        @Deprecated
        private String publisherInterstitialId;
        private String productSku;
        private String subscriptionSku;
        // Prefix for concat keys on remote config...
        private String appPrefix;

        private boolean tester;
        private boolean adEnabled = true;
        private boolean enableCheckMobills;

        public Builder() { }

        public Builder setLogo(@DrawableRes int logo) {
            this.logo = logo;
            return this;
        }

        public Builder setInterstitialId(String interstitialId) {
            this.interstitialId = interstitialId;
            return this;
        }

        public Builder setExpensiveInterstitialId(String expensiveInterstitialId) {
            this.expensiveInterstitialId = expensiveInterstitialId;
            return this;
        }

        public Builder setMidInterstitialId(String midInterstitialId){
            this.midInterstitialId = midInterstitialId;
            return this;
        }

        public Builder setNativeAdId(String nativeAdId) {
            this.nativeAdId = nativeAdId;
            return this;
        }

        public Builder setExpensiveNativeAdId(String expensiveNativeAdId) {
            this.expensiveNativeAdId = expensiveNativeAdId;
            return this;
        }

        public Builder setMidNativeAdId(String midNativeAdId){
            this.midInterstitialId = midNativeAdId;
            return this;
        }

        public Builder setTwoFloorsNativeAd(String expensiveNativeAdId, String midNativeAdId){
            this.expensiveNativeAdId = expensiveNativeAdId;
            this.midNativeAdId = midNativeAdId;
            return this;
        }

        public Builder setTwoFloorsInterstitial(String expensiveInterstitialId, String midInterstitialId) {
            this.expensiveInterstitialId = expensiveInterstitialId;
            this.midInterstitialId = midInterstitialId;
            return this;
        }

        public Builder setBannerAdId(String bannerAdId) {
            this.bannerAdId = bannerAdId;
            return this;
        }

        public Builder setRewardedVideoId(String rewardedVideoId) {
            this.rewardedVideoId = rewardedVideoId;
            return this;
        }

        @Deprecated
        public Builder setPublisherInterstitialId(String publisherInterstitialId) {
            this.publisherInterstitialId = publisherInterstitialId;
            return this;
        }

        public Builder enablePurchace(String productSku) {
            this.productSku = productSku;
            return this;
        }

        public Builder enableSubscription(String subscriptionSku) {
            this.productSku = subscriptionSku;
            return this;
        }

        public Builder setAppPrefix(String appPrefix) {
            this.appPrefix = appPrefix;
            return this;
        }

        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder setTester(boolean tester) {
            this.tester = tester;
            return this;
        }

        public Builder setAdEnabled(boolean adEnabled) {
            this.adEnabled = adEnabled;
            return this;
        }

        public Builder enableCheckMobills(boolean enableCheckMobills) {
            this.enableCheckMobills = enableCheckMobills;
            return this;
        }

        public IRAdsInit build(Context context) {
            this.IRAdsInit = new IRAdsInit(this);

            if (ConfigUtil.APP_ID != null && context != null)
                MobileAds.initialize(context, ConfigUtil.APP_ID);

            return this.IRAdsInit;
        }

    }

}