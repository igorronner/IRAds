package com.igorronner.irinterstitial.init;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.igorronner.irinterstitial.R;
import com.igorronner.irinterstitial.dto.RemoteConfigDTO;
import com.igorronner.irinterstitial.preferences.MainPreference;
import com.igorronner.irinterstitial.services.IRInterstitialService;
import com.igorronner.irinterstitial.services.IRRewardedVideoAdService;
import com.igorronner.irinterstitial.services.ManagerNativeAd;
import com.igorronner.irinterstitial.services.RemoteConfigService;
import com.igorronner.irinterstitial.views.SplashActivity;

@SuppressWarnings({"WeakerAccess", "unused"})
public class IRAds implements RemoteConfigService.ServiceListener<RemoteConfigDTO> {

    private static final int STOPPED = 910;
    private static final int RESUMED = 967;

    private Activity activity;
    private RemoteConfigDTO remoteConfigDTO;
    private ManagerNativeAd managerNativeAd;
    private int state = 0;

    private IRAds() {
    }

    private IRAds(Activity activity) {
        this.activity = activity;
    }

    public static IRAds newInstance(Activity activity) {
        final ManagerNativeAd manager = new ManagerNativeAd(activity)
                .setAdmobAdUnitId(ConfigUtil.NATIVE_AD_ID)
                .setExpensiveAdmobAdUnitId(ConfigUtil.EXPENSIVE_NATIVE_AD_ID)
                .setBannerAdmobAdUnitId(ConfigUtil.BANNER_AD_ID);

        final IRAds irAds = new IRAds(activity);
        irAds.loadRemoteConfig(irAds);
        irAds.setManagerNativeAd(manager);

        return irAds;
    }

    public static boolean isPremium(Context context) {
        return MainPreference.isPremium(context);
    }

    @Override
    public void onComplete(RemoteConfigDTO result) {
        this.remoteConfigDTO = result;
    }

    public Activity getActivity() {
        return activity;
    }

    private void setManagerNativeAd(ManagerNativeAd managerNativeAd) {
        this.managerNativeAd = managerNativeAd;
    }

    public void forceShowInterstitial() {
        forceShowInterstitial(true);
    }

    public void forceShowInterstitial(final Boolean finish) {
        new IRInterstitialService(IRAds.this).forceShowInterstitial(finish);
    }

    public void forceShowInterstitialBeforeIntent(final Intent intent) {
        forceShowInterstitialBeforeIntent(intent, false);
    }

    public void forceShowInterstitialBeforeIntent(final Intent intent, final boolean finishAll) {
        new IRInterstitialService(IRAds.this)
                .forceShowInterstitialBeforeIntent(intent, finishAll);
    }

    public void forceShowInterstitialBeforeIntent(
            final Fragment fragment,
            final @IdRes int containerViewId,
            final FragmentActivity fragmentActivity) {
        new IRInterstitialService(IRAds.this)
                .forceShowInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity);
    }

    public void showInterstitialBeforeFragment(
            final Fragment fragment,
            final @IdRes int containerViewId,
            final FragmentActivity fragmentActivity) {
        new IRInterstitialService(IRAds.this)
                .showInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity);
    }

    public void showInterstitial() {
        showInterstitial(true);
    }

    public void showInterstitial(final boolean finish) {
        new IRInterstitialService(IRAds.this).showInterstitial(finish);
    }

    public void showInterstitialBeforeIntent(final Intent intent, final boolean finishAll) {
        new IRInterstitialService(IRAds.this).showInterstitialBeforeIntent(intent, finishAll);
    }

    public void showInterstitialBeforeIntent(final Intent intent) {
        showInterstitialBeforeIntent(intent, false);
    }

    @Deprecated
    public void showInterstitialOnFinish() {
        if (remoteConfigDTO != null) {
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

    public void openDialogRewardedVideo(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.text_title_dias_premium)
                .setMessage(R.string.text_message_assisa_ao_video)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.text_assistir, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showRewardedVideo();
                    }
                });

        final AlertDialog alertDialog = builder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRewardedVideo() {
        new IRRewardedVideoAdService(activity).showRewardedVideo(
                new IRRewardedVideoAdService.OnRewardedVideoListener() {
                    @Override
                    public void onRewardedVideoAdClosed(boolean earned) {
                        if (earned && !activity.isFinishing()) {
                            activity.recreate();
                        }
                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad() {
                        if (activity.isFinishing()) {
                            return;
                        }

                        Toast.makeText(
                                activity,
                                R.string.text_message_nenhum_video_encontrado,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    public void showRewardedVideo(IRRewardedVideoAdService.OnRewardedVideoListener listener) {
        new IRRewardedVideoAdService(activity).showRewardedVideo(listener);
    }

    public void openSplashScreen() {
        if (!isPremium(activity)) {
            activity.startActivity(new Intent(activity, SplashActivity.class));
        }
    }

    public void loadRemoteConfig(RemoteConfigService.ServiceListener<RemoteConfigDTO> serviceListener) {
        RemoteConfigService.getInstance(activity).loadRemoteConfig(serviceListener);
    }

    public void loadNativeAd(boolean showProgress, UnifiedNativeAdView unifiedNativeAdView) {
        managerNativeAd.setShowProgress(showProgress).loadNativeAd(null, unifiedNativeAdView);
    }

    public void loadNativeAd(ViewGroup cardView, boolean showProgress, UnifiedNativeAdView unifiedNativeAdView) {
        managerNativeAd.setShowProgress(showProgress).loadNativeAd(cardView, unifiedNativeAdView);
    }

    public void loadNativeAd(ViewGroup cardView, UnifiedNativeAdView unifiedNativeAdView) {
        managerNativeAd.setShowProgress(false).loadNativeAd(cardView, unifiedNativeAdView);
    }

    public void loadNativeAd() {
        loadNativeAd(false, (UnifiedNativeAdView) activity.findViewById(R.id.adViewNative));
    }

    public void loadNativeAd(boolean showProgress) {
        loadNativeAd(showProgress, (UnifiedNativeAdView) activity.findViewById(R.id.adViewNative));
    }

    public void loadNativeOrBannerAd(
            ViewGroup parent, UnifiedNativeAdView adView, boolean progress) {
        loadNativeOrBannerAd(parent, adView, AdSize.MEDIUM_RECTANGLE, progress);
    }

    public void loadNativeOrBannerAd(
            ViewGroup parent, UnifiedNativeAdView adView, AdSize adSize, boolean progress) {
        managerNativeAd.setShowProgress(progress).loadNativeOrBannerAd(parent, adView, adSize);
    }

    public void onStop() {
        state = STOPPED;
    }

    public void onResume() {
        state = RESUMED;
    }

    public boolean isStopped() {
        return state == STOPPED;
    }

    public boolean isResumed() {
        return state == RESUMED;
    }

}