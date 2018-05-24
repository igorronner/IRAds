package com.igorronner.irinterstitial.services;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.igorronner.irinterstitial.R;
import com.igorronner.irinterstitial.init.IRAds;
import com.igorronner.irinterstitial.preferences.MainPreference;

import java.util.List;


public class ManagerNativeAd {

    private static ManagerNativeAd instance;
    private Context context;
    private boolean showProgress;
    private ProgressBar progressBar;
    private String admobAdUnitId;


    public static ManagerNativeAd getInstance(Context context) {
        if(instance == null){
            instance = new ManagerNativeAd();
        }
        instance.setShowProgress(false);
        instance.setContext(context);

        return instance;
    }

    private ManagerNativeAd(){

    }

    public ManagerNativeAd setAdmobAdUnitId(String admobAdUnitId) {
        this.admobAdUnitId = admobAdUnitId;
        return this;
    }

    public ManagerNativeAd setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
        return this;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void loadAppInstallAdView(final View adCard, final NativeAppInstallAdView adView){
        if (MainPreference.isPremium(context)){
            adView.getChildAt(0).setVisibility(View.GONE);
            if (adCard!=null)
                adCard.setVisibility(View.GONE);
            return;
        }

        adView.getChildAt(0).setVisibility(View.INVISIBLE);
        if (showProgress) {
            progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
            progressBar.setIndeterminate(true);

            RelativeLayout.LayoutParams params = new
                    RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rl = new RelativeLayout(context);

            rl.setGravity(Gravity.CENTER);
            rl.addView(progressBar);

            adView.addView(rl, params);
        }

        AdLoader.Builder builder = new AdLoader.Builder(context,  admobAdUnitId);
        builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                adView.getChildAt(0).setVisibility(View.VISIBLE);
                if (showProgress){
                    progressBar.setVisibility(View.GONE);
                    setAdmobAdUnitId(null);
                }
                populateAppInstallAdView(adView, nativeAppInstallAd);
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                adView.getChildAt(0).setVisibility(View.INVISIBLE);

            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }
    public void loadAppInstallAdView(final NativeAppInstallAdView adView){
        loadAppInstallAdView(null, adView);
    }


    public void loadNativeAdContent(final NativeContentAdView adView){

        if (MainPreference.isPremium(context)){
            adView.getChildAt(0).setVisibility(View.INVISIBLE);
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(context, admobAdUnitId);

        builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {

            @Override
            public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                populateContentAd(adView, nativeContentAd);
            }
        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void populateContentAd(NativeContentAdView adView, NativeContentAd nativeContentAd){
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        try {
            if (images.size() > 0) {
                ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
            }
        } catch (Exception exception){
            ((ImageView) adView.getImageView()).setVisibility(View.GONE);
        }


        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }

    private void populateAppInstallAdView(NativeAppInstallAdView adView, NativeAppInstallAd nativeAppInstallAd) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
       // adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(
                nativeAppInstallAd.getIcon().getDrawable());

        MediaView mediaView = adView.findViewById(R.id.appinstall_media);
        ImageView mainImageView = adView.findViewById(R.id.appinstall_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);

        } else {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);
            // At least one image is guaranteed.
            try {

                List<NativeAd.Image> images = nativeAppInstallAd.getImages();
                mainImageView.setImageDrawable(images.get(0).getDrawable());
            } catch (Exception exception){
                mainImageView.setVisibility(View.GONE);
            }

        }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

}