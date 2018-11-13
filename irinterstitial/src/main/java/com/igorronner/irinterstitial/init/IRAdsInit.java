package com.igorronner.irinterstitial.init;

import android.content.Context;
import android.support.annotation.DrawableRes;

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
        ConfigUtil.NATIVE_AD_ID = builder.nativeAdId;
        ConfigUtil.PUBLISHER_INTERSTITIAL_ID = builder.publisherInterstitialId;
        ConfigUtil.PRODUCT_SKU = builder.productSku;
        ConfigUtil.APP_PREFIX = builder.appPrefix;
    }

    public static class Builder {
        @DrawableRes
        private int logo;
        private IRAdsInit IRAdsInit;
        private String appId;
        private String interstitialId;
        private String nativeAdId;
        private String publisherInterstitialId;
        private String productSku;
        // Prefix for concat keys on remote config...
        private String appPrefix;

        public Builder() {
        }

        public Builder setLogo(@DrawableRes int logo) {
            this.logo = logo;
            return this;
        }

        public Builder setInterstitialId(String interstitialId) {
            this.interstitialId = interstitialId;
            return this;
        }

        public Builder setNativeAdId(String nativeAdId) {
            this.nativeAdId = nativeAdId;
            return this;
        }

        public Builder setPublisherInterstitialId(String publisherInterstitialId) {
            this.publisherInterstitialId = publisherInterstitialId;
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

        public Builder setAppId(String appId){
            this.appId = appId;
            return this;
        }

        public IRAdsInit build(Context context) {
            this.IRAdsInit = new IRAdsInit(this);

            if (ConfigUtil.APP_ID !=null && context !=null)
                MobileAds.initialize(context, ConfigUtil.APP_ID);

            return this.IRAdsInit;
        }

    }


}