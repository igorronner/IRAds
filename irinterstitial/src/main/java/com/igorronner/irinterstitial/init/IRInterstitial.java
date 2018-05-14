package com.igorronner.irinterstitial.init;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.DrawableRes;

import com.igorronner.irinterstitial.views.SplashActivity;


public class IRInterstitial {

    public static IRInterstitial.Builder startInit(String googleClientId, String  addUnitId) {
        return new IRInterstitial.Builder(googleClientId, addUnitId);
    }

    private IRInterstitial(final IRInterstitial.Builder builder) {
        ConfigUtil.LOGO = builder.logo;
        ConfigUtil.GOOGLE_CLIENT_ID = builder.googleClientId;
        ConfigUtil.AD_UNIT_ID = builder.addUnitId;
    }

    public static class Builder {
        @DrawableRes
        private int logo;
        private String googleClientId;
        private IRInterstitial IRInterstitial;
        private String addUnitId;

        public Builder(String googleClientId, String addUnitId) {
            this.googleClientId = googleClientId;
            this.addUnitId = addUnitId;
        }



        public Builder setLogo(@DrawableRes int logo) {
            this.logo = logo;
            return this;
        }


        public IRInterstitial build() {
            this.IRInterstitial = new IRInterstitial(this);
            return this.IRInterstitial;
        }

    }

    public static void showInterstitial(Activity activity){
        new IRInterstitialService(activity).showInterstitial();
    }

    public static void openSplashScren(Activity activity){
        activity.startActivity(new Intent(activity, SplashActivity.class));
    }

}