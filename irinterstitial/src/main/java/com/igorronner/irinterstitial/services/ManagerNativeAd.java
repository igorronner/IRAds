package com.igorronner.irinterstitial.services;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.igorronner.irinterstitial.R;
import com.igorronner.irinterstitial.preferences.MainPreference;

import org.jetbrains.annotations.Nullable;

import java.util.List;


public class ManagerNativeAd {

    private Context context;
    private String admobAdUnitId;
    private String expensiveAdmobAdUnitId;
    private String bannerAdmobAdUnitId;
    private boolean showProgress;

    @Deprecated
    private ProgressBar progressBar;

    public ManagerNativeAd(Context context) {
        this.context = context;
        this.showProgress = false;
    }

    public ManagerNativeAd setAdmobAdUnitId(String admobAdUnitId) {
        this.admobAdUnitId = admobAdUnitId;
        return this;
    }

    public ManagerNativeAd setExpensiveAdmobAdUnitId(String expensiveAdmobAdUnitId) {
        this.expensiveAdmobAdUnitId = expensiveAdmobAdUnitId;
        return this;
    }

    public ManagerNativeAd setBannerAdmobAdUnitId(String bannerAdmobAdUnitId) {
        this.bannerAdmobAdUnitId = bannerAdmobAdUnitId;
        return this;
    }

    public ManagerNativeAd setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
        return this;
    }

    public void loadNativeAd(final ViewGroup adCard, final UnifiedNativeAdView adView) {
        loadNativeAd(adCard, adView, true);
    }

    public void loadExpensiveNativeAd(final ViewGroup adCard, final UnifiedNativeAdView adView) {
        if (MainPreference.isPremium(context)) {
            adView.setVisibility(View.GONE);
            if (adCard != null) {
                adCard.setVisibility(View.GONE);
            }
            return;
        }

        final ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
        if (showProgress) {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rl = new RelativeLayout(context);
            rl.setGravity(Gravity.CENTER);
            rl.addView(progressBar);
            adView.addView(rl, params);
        }
        progressBar.setVisibility(View.VISIBLE);
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();


        final String unifiedNativeAdType = "EXPENSIVE";
        AdLoader.Builder builder = new AdLoader.Builder(context, expensiveAdmobAdUnitId)
                .withNativeAdOptions(adOptions)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        progressBar.setVisibility(View.GONE);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        if (adCard != null) {
                            adCard.removeAllViews();
                            adCard.addView(adView);
                        }
                        final String event = "Mostrou UnifiedNativeAd: " + unifiedNativeAdType;
                        Log.d(ManagerNativeAd.class.getSimpleName(), event);
                        new AnalyticsService(context).logEvent(event);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        progressBar.setVisibility(View.GONE);
                        adView.setVisibility(View.GONE);
                        if(adCard != null){
                            adCard.setVisibility(View.GONE);
                        }
                    }
                });

        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }



    private void loadNativeAd(final ViewGroup adCard, final UnifiedNativeAdView adView, final boolean expensive) {
        if (MainPreference.isPremium(context)) {
            adView.setVisibility(View.GONE);
            if (adCard != null) {
                adCard.setVisibility(View.GONE);
            }
            return;
        }

        final ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
        if (showProgress) {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rl = new RelativeLayout(context);
            rl.setGravity(Gravity.CENTER);
            rl.addView(progressBar);
            adView.addView(rl, params);
        }
        progressBar.setVisibility(View.VISIBLE);
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();


        final String unifiedNativeAdType = expensive ? "EXPENSIVE" : "NORMAL";
        AdLoader.Builder builder = new AdLoader.Builder(context, expensive ? expensiveAdmobAdUnitId : admobAdUnitId)
                .withNativeAdOptions(adOptions)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        progressBar.setVisibility(View.GONE);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        if (adCard != null) {
                            adCard.removeAllViews();
                            adCard.addView(adView);
                        }
                        final String event = "Mostrou UnifiedNativeAd: " + unifiedNativeAdType;
                        Log.d(ManagerNativeAd.class.getSimpleName(), event);
                        new AnalyticsService(context).logEvent(event);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        progressBar.setVisibility(View.GONE);
                        if (expensive) {
                            setExpensiveAdmobAdUnitId(null);
                            loadNativeAd(adCard, adView, false);
                        } else {
                            setAdmobAdUnitId(null);
                            adView.setVisibility(View.INVISIBLE);
                        }

                        final String event = "Falhou UnifiedNativeAd: " + unifiedNativeAdType;
                        Log.d(ManagerNativeAd.class.getSimpleName(), event);
                        new AnalyticsService(context).logEvent(event);
                    }
                });

        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    /**
     * Método responsável por pedir um NativeAd caso contrário, não existir naquele momento
     * requisita um BannerAd no local
     *
     * @param parent ViewGroup root
     * @param adView UnifiedNativeAdView (Native)
     * @param adSize AdSize
     */
    public void loadNativeOrBannerAd(
            @Nullable final ViewGroup parent,
            @NonNull final UnifiedNativeAdView adView,
            final AdSize adSize) {

        if (MainPreference.isPremium(context)) {
            adView.setVisibility(View.GONE);
            if (parent != null) {
                parent.setVisibility(View.GONE);
            }
            return;
        }

        final ProgressBar progressBar = new ProgressBar(
                context, null, android.R.attr.progressBarStyleSmall);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        final RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.addView(progressBar);
        relativeLayout.setTag("ProgressContainer");

        if (adView.findViewWithTag("ProgressContainer") != null) {
            adView.removeView(adView.findViewWithTag("ProgressContainer"));
        }

        adView.addView(relativeLayout, params);

        if (showProgress) {
            progressBar.setVisibility(View.VISIBLE);
        }

        final VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        final NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        final AdListener adListener = new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                final PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                        .addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB")
                        .build();

                final PublisherAdView adViewBanner = new PublisherAdView(context);
                adViewBanner.setAdSizes(adSize);
                adViewBanner.setAdUnitId(bannerAdmobAdUnitId);
                adViewBanner.loadAd(adRequest);
                adViewBanner.setTag("PublisherAdView");

                if (parent != null && parent.findViewWithTag("PublisherAdView") != null) {
                    parent.removeView(parent.findViewWithTag("PublisherAdView"));
                }

                if (parent != null) {
                    parent.addView(adViewBanner);
                }

                adViewBanner.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        progressBar.setVisibility(View.GONE);

                        showEvent("Mostrou Banner: Normal");
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        progressBar.setVisibility(View.GONE);
                        adView.setVisibility(View.GONE);

                        if (parent != null) {
                            parent.removeView(adViewBanner);
                        }

                        showEvent("Falhou Banner: Normal " + i);
                    }
                });

                showEvent("Falhou UnifiedNativeAd: EXPENSIVE");
            }
        };

        final AdLoader.Builder builder = new AdLoader.Builder(context, expensiveAdmobAdUnitId)
                .withNativeAdOptions(adOptions)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        progressBar.setVisibility(View.GONE);

                        populateUnifiedNativeAdView(unifiedNativeAd, adView);

                        showEvent("Mostrou UnifiedNativeAd: EXPENSIVE");
                    }
                })
                .withAdListener(adListener);

        final AdRequest adRequest = new AdRequest.Builder()
                .build();

        final AdLoader adLoader = builder.build();
        adLoader.loadAd(adRequest);
    }

    @Deprecated
    public void loadAppInstallAdView(final View adCard, final NativeAppInstallAdView adView) {
        if (MainPreference.isPremium(context)) {
            adView.getChildAt(0).setVisibility(View.GONE);
            if (adCard != null)
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

        AdLoader.Builder builder = new AdLoader.Builder(context, admobAdUnitId);

        builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                adView.getChildAt(0).setVisibility(View.VISIBLE);
                if (showProgress) {
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

    @Deprecated
    public void loadAppInstallAdView(final NativeAppInstallAdView adView) {
        loadAppInstallAdView(null, adView);
    }

    @Deprecated
    public void loadNativeAdContent(final NativeContentAdView adView) {

        if (MainPreference.isPremium(context)) {
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

    @Deprecated
    private void populateContentAd(NativeContentAdView adView, NativeContentAd nativeContentAd) {
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
        } catch (Exception exception) {
            ((ImageView) adView.getImageView()).setVisibility(View.GONE);
        }


        try {
            // Some aren't guaranteed, however, and should be checked.
            NativeAd.Image logoImage = nativeContentAd.getLogo();

            if (logoImage == null || logoImage.getDrawable() == null) {
                adView.getLogoView().setVisibility(View.INVISIBLE);
            } else {
                ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
                adView.getLogoView().setVisibility(View.VISIBLE);
            }

        } catch (Exception exception) {

        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }

    @Deprecated
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

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
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
            } catch (Exception exception) {
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

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        ConstraintLayout contentLayout = adView.findViewById(R.id.adViewNativeContent);
        if (contentLayout != null) {
            contentLayout.setVisibility(View.VISIBLE);
        }

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            if (adView.getBodyView() != null) {
                adView.getBodyView().setVisibility(View.GONE);
            }
        } else {
            if (adView.getBodyView() != null) {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (nativeAd.getCallToAction() == null) {
            if (adView.getCallToActionView() != null) {
                adView.getCallToActionView().setVisibility(View.GONE);
            }
        } else {
            if (adView.getCallToActionView() != null) {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (nativeAd.getIcon() == null) {
            if (adView.getIconView() != null) {
                adView.getIconView().setVisibility(View.GONE);
            }
        } else {
            if (adView.getIconView() != null) {
                adView.getIconView().setVisibility(View.VISIBLE);
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            }
        }

        if (nativeAd.getPrice() == null) {
            if (adView.getPriceView() != null) {
                adView.getPriceView().setVisibility(View.GONE);
            }
        } else {
            if (adView.getPriceView() != null) {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        }

        if (nativeAd.getStore() == null) {
            if (adView.getStoreView() != null) {
                adView.getStoreView().setVisibility(View.GONE);
            }
        } else {
            if (adView.getStoreView() != null) {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        }

        if (nativeAd.getStarRating() == null) {
            if (adView.getStarRatingView() != null) {
                adView.getStarRatingView().setVisibility(View.GONE);
            }
        } else {
            if (adView.getStarRatingView() != null) {
                adView.getStarRatingView().setVisibility(View.VISIBLE);
                ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            }
        }

        if (nativeAd.getAdvertiser() == null) {
            if (adView.getAdvertiserView() != null) {
                adView.getAdvertiserView().setVisibility(View.GONE);
            }
        } else {
            if (adView.getAdvertiserView() != null) {
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            }
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    private void showEvent(String message) {
        Log.d(ManagerNativeAd.class.getSimpleName(), message);
        new AnalyticsService(context).logEvent(message);
    }

}