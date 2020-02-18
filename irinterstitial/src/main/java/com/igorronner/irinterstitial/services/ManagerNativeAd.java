package com.igorronner.irinterstitial.services;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
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
import com.igorronner.irinterstitial.enums.FloorEnum;
import com.igorronner.irinterstitial.preferences.MainPreference;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.igorronner.irinterstitial.enums.FloorEnum.HIGH_FLOOR;
import static com.igorronner.irinterstitial.enums.FloorEnum.MID_FLOOR;
import static com.igorronner.irinterstitial.enums.FloorEnum.NO_FLOOR;


public class ManagerNativeAd {

    private Context context;
    private String admobAdUnitId;
    private String midAdmobAdUnitId;
    private String expensiveAdmobAdUnitId;
    private String bannerAdmobAdUnitId;
    private boolean showProgress;
    private UnifiedNativeAd unifiedNativeAdCached;
    private UnifiedNativeAd unifiedNativeAdExpensiveCached;

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

    public ManagerNativeAd setMidAdmobAdUnitId(String midAdmobAdUnitId) {
        this.midAdmobAdUnitId = midAdmobAdUnitId;
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
        loadNativeAd(adCard, adView, HIGH_FLOOR);
    }

    /**
     * Método responsável por pedir um NativeAd com floor mais alto caso contrário, não exibir nenhuma propaganda
     *
     * @param adCard ViewGroup root
     * @param adView UnifiedNativeAdView (Native)
     */
    public void loadExpensiveNativeAd(final ViewGroup adCard, final UnifiedNativeAdView adView) {
        if (MainPreference.isPremium(context)) {
            adView.setVisibility(View.GONE);
            if (adCard != null) {
                adCard.setVisibility(View.GONE);
            }
            return;
        }

        if (unifiedNativeAdExpensiveCached != null){
            populateUnifiedNativeAdView(unifiedNativeAdExpensiveCached, adView);
            unifiedNativeAdExpensiveCached = null;
            loadAd(HIGH_FLOOR);
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
                        unifiedNativeAdExpensiveCached = unifiedNativeAd;
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

    private void loadNativeAd(
            final ViewGroup adCard,
            final UnifiedNativeAdView adView,
            final FloorEnum floorEnum
    ) {

        if (MainPreference.isPremium(context)) {
            adView.setVisibility(View.GONE);
            if (adCard != null) {
                adCard.setVisibility(View.GONE);
            }
            return;
        }

        if (unifiedNativeAdCached != null){
            populateUnifiedNativeAdView(unifiedNativeAdCached, adView);
            unifiedNativeAdCached = null;
            loadAd(HIGH_FLOOR);
            return;
        }

        String unifiedNativeAdType = "";
        String adUnitId = "";

        if (floorEnum == HIGH_FLOOR) {
            if (isIdValid(expensiveAdmobAdUnitId)) {
                adUnitId = expensiveAdmobAdUnitId;
                unifiedNativeAdType = HIGH_FLOOR.name();
            } else {
                loadNativeAd(adCard, adView, MID_FLOOR);
                return;
            }
        }

        if (floorEnum == MID_FLOOR) {
            if (isIdValid(midAdmobAdUnitId)) {
                adUnitId = midAdmobAdUnitId;
                unifiedNativeAdType = MID_FLOOR.name();
            } else {
                loadNativeAd(adCard, adView, NO_FLOOR);
                return;
            }
        }

        if (floorEnum == NO_FLOOR) {
            if (isIdValid(admobAdUnitId)){
                adUnitId = admobAdUnitId;
                unifiedNativeAdType = NO_FLOOR.name();
            } else {
                adView.setVisibility(View.INVISIBLE);
                return;
            }
        }

        final String eventSuccess = "Mostrou_UnifiedNativeAd_" + unifiedNativeAdType;
        final String eventFailed = "Falhou_UnifiedNativeAd_" + unifiedNativeAdType;

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

        AdLoader.Builder builder = new AdLoader.Builder(context, adUnitId)
                .withNativeAdOptions(adOptions)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        unifiedNativeAdCached = unifiedNativeAd;
                        progressBar.setVisibility(View.GONE);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        if (adCard != null) {
                            adCard.removeAllViews();
                            adCard.addView(adView);
                        }
                        Log.d(ManagerNativeAd.class.getSimpleName(), eventSuccess);
                        new AnalyticsService(context).logEvent(eventSuccess);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        progressBar.setVisibility(View.GONE);

                        if (floorEnum == HIGH_FLOOR && isIdValid(expensiveAdmobAdUnitId)) {
                            loadNativeAd(adCard, adView, MID_FLOOR);
                        } else if (floorEnum == MID_FLOOR && isIdValid(midAdmobAdUnitId)) {
                            loadNativeAd(adCard, adView, NO_FLOOR);
                        } else {
                            adView.setVisibility(View.INVISIBLE);
                        }

                        Log.d(ManagerNativeAd.class.getSimpleName(), eventFailed);
                        new AnalyticsService(context).logEvent(eventFailed);
                    }
                });

        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void loadAd(final FloorEnum floorEnum){

        String adUnitId = "";

        if (floorEnum == HIGH_FLOOR) {
            if (isIdValid(expensiveAdmobAdUnitId)) {
                adUnitId = expensiveAdmobAdUnitId;
            } else {
                loadAd(MID_FLOOR);
            }
        }

        if (floorEnum == MID_FLOOR) {
            if (isIdValid(midAdmobAdUnitId)) {
                adUnitId = midAdmobAdUnitId;
            } else {
                loadAd(NO_FLOOR);
            }
        }

        if (floorEnum == NO_FLOOR && isIdValid(admobAdUnitId)) {
            adUnitId = admobAdUnitId;
        }

        if (adUnitId.isEmpty())
            showEvent("erro_ad_unit_id_vazio");

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        AdLoader.Builder builder = new AdLoader.Builder(context, adUnitId)
                .withNativeAdOptions(adOptions)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        if (floorEnum == HIGH_FLOOR)
                            unifiedNativeAdExpensiveCached = unifiedNativeAd;

                        unifiedNativeAdCached = unifiedNativeAd;
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        if (floorEnum == HIGH_FLOOR && isIdValid(expensiveAdmobAdUnitId)) {
                            loadAd(MID_FLOOR);
                        } else if (floorEnum == MID_FLOOR && isIdValid(midAdmobAdUnitId)) {
                            loadAd(NO_FLOOR);
                        }
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

        if (unifiedNativeAdCached != null){
            populateUnifiedNativeAdView(unifiedNativeAdCached, adView);
            unifiedNativeAdCached = null;
            loadAd(HIGH_FLOOR);
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

                        showEvent("Mostrou_Banner_Normal");
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        progressBar.setVisibility(View.GONE);
                        adView.setVisibility(View.GONE);
                        if (parent != null) {
                            parent.removeView(adViewBanner);
                        }
                    }
                });

            }
        };

        final AdLoader.Builder builder = new AdLoader.Builder(context, expensiveAdmobAdUnitId)
                .withNativeAdOptions(adOptions)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        unifiedNativeAdCached = unifiedNativeAd;
                        progressBar.setVisibility(View.GONE);

                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                    }
                })
                .withAdListener(adListener);

        final AdRequest adRequest = new AdRequest.Builder()
                .build();

        final AdLoader adLoader = builder.build();
        adLoader.loadAd(adRequest);
    }

    public void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
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

    private boolean isIdValid(String admobAdUnitId){
        return admobAdUnitId != null && !admobAdUnitId.isEmpty();
    }

}