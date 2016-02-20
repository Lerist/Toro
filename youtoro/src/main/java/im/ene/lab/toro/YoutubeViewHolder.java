/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.toro;

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.view.View;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by eneim on 2/15/16.
 */
public abstract class YoutubeViewHolder extends ToroViewHolder
    implements YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaybackEventListener,
    YouTubePlayer.OnInitializedListener {

  /**
   * This setup will offer {@link YouTubePlayer.PlayerStyle#CHROMELESS} to youtube player
   */
  protected static final int CHROMELESS = 0b01;
  /**
   * This setup will offer {@link YouTubePlayer.PlayerStyle#MINIMAL} to youtube player
   */
  protected static final int MINIMUM = 0b10;
  private static final String TAG = "YoutubeViewHolder";
  /**
   * Parent Adapter which holds some important controllers
   */
  protected final YoutubeListAdapter mParent;

  /**
   * Id for {@link YouTubePlayerSupportFragment}, will be generated manually and must be set for
   * proper view
   */
  protected final int mFragmentId;

  private final YoutubeViewHolderHelper mHelper;
  protected YouTubePlayerSupportFragment mYoutubeFragment;
  private int seekPosition = 0;
  private boolean isSeeking = false;
  private boolean isStarting = false;

  public YoutubeViewHolder(View itemView, YoutubeListAdapter parent) {
    super(itemView);
    this.mParent = parent;
    if (this.mParent.mFragmentManager == null) {
      throw new IllegalArgumentException(
          "This View requires a YoutubeListAdapter parent which holds a non-null FragmentManager");
    }
    this.mHelper = new YoutubeViewHolderHelper();
    this.mFragmentId = ToroUtils.generateViewId();
  }

  @Override public final boolean wantsToPlay() {
    return super.visibleAreaOffset() >= 0.95f;  // Actually Youtube wants 1.0f;
  }

  final boolean isStarting() {
    return this.isStarting;
  }

  @Override public final boolean isAbleToPlay() {
    return true;  // Always true. Because Youtube API won't listen to this...
  }

  @CallSuper @Override public void onViewHolderBound() {
    super.onViewHolderBound();
    if (itemView.findViewById(mFragmentId) == null) {
      throw new RuntimeException("View with Id: " + mFragmentId + " must be setup");
    }

    if ((mYoutubeFragment =
        (YouTubePlayerSupportFragment) mParent.mFragmentManager.findFragmentById(mFragmentId))
        == null) {
      mYoutubeFragment = YouTubePlayerSupportFragment.newInstance();
      // Add youtube player fragment to this ViewHolder
      mParent.mFragmentManager.beginTransaction().replace(mFragmentId, mYoutubeFragment).commit();
    }
  }

  @CallSuper @Override public void start() {
    isStarting = true;
    // Release current youtube player first. Prevent resource conflict
    if (mParent.mYoutubePlayer != null) {
      mParent.mYoutubePlayer.release();
    }
    // Re-setup the Player. This is annoying though.
    if (mYoutubeFragment != null) {
      mYoutubeFragment.initialize(Toro.sYoutoro.apiKey, this);
    }
  }

  @CallSuper @Override public void pause() {
    isStarting = false;
    if (mParent.mYoutubePlayer != null) {
      try {
        mParent.mYoutubePlayer.pause();
      } catch (IllegalStateException er) {
        er.printStackTrace();
      }
    }
  }

  @Override public final int getDuration() {
    try {
      return mParent.mYoutubePlayer != null ? mParent.mYoutubePlayer.getDurationMillis() : -1;
    } catch (IllegalStateException er) {
      er.printStackTrace();
      return -1;
    }
  }

  @Override public final int getCurrentPosition() {
    try {
      return mParent.mYoutubePlayer != null ? mParent.mYoutubePlayer.getCurrentTimeMillis() : 0;
    } catch (IllegalStateException er) {
      er.printStackTrace();
      return 0;
    }
  }

  @CallSuper @Override public final void seekTo(int pos) {
    isSeeking = true;
    seekPosition = pos;
  }

  @Override public final boolean isPlaying() {
    try {
      // is loading the video or playing it
      return isStarting || (mParent.mYoutubePlayer != null && mParent.mYoutubePlayer.isPlaying());
    } catch (IllegalStateException er) {
      er.printStackTrace();
      return isStarting;
    }
  }

  // Youtube video id for this view. This method should be used dynamically
  public abstract String getYoutubeVideoId();

  @CallSuper @Override public void onLoading() {
    mHelper.onLoading();
  }

  @CallSuper @Override public void onLoaded(String videoId) {
    mHelper.onLoaded(videoId);
    mHelper.onPrepared(this, itemView, itemView.getParent(), null);
  }

  @CallSuper @Override public void onAdStarted() {
    mHelper.onAdStarted();
  }

  @CallSuper @Override public final void onVideoStarted() {
    mHelper.onVideoStarted(this);
  }

  @CallSuper @Override public final void onVideoEnded() {
    mHelper.onCompletion(this, null);
    mHelper.onVideoEnded(this);
  }

  @CallSuper @Override public void onError(YouTubePlayer.ErrorReason errorReason) {
    mHelper.onError(this, null, 0, 0);
    mHelper.onError(this, errorReason);
  }

  @CallSuper @Override public final void onPlaying() {
    isStarting = false;
    mHelper.onPlaying();
  }

  // Paused by API's button. Should not dispatch any custom behavior.
  @CallSuper @Override public final void onPaused() {
    mHelper.onPaused();
  }

  // The method is called once RIGHT BEFORE A VIDEO GOT LOADED (by Youtube Player API)
  // And once again after the Player completes playback.
  @CallSuper @Override public final void onStopped() {
    mHelper.onStopped();
  }

  @CallSuper @Override public void onBuffering(boolean isBuffering) {
    mHelper.onBuffering(isBuffering);
  }

  // Called internal. Youtube's Playback event is internally called by API, so User should not
  // dispatch them
  @CallSuper @Override public final void onSeekTo(int position) {
    seekPosition = position;
    isSeeking = true;
  }

  /**
   * This library will force user to use either {@link YouTubePlayer.PlayerStyle#MINIMAL} or {@link
   * YouTubePlayer.PlayerStyle#CHROMELESS}. User should override this to provide her expected UI
   */
  @PlayerStyle protected int getPlayerStyle() {
    return MINIMUM;
  }

  @CallSuper @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,
      boolean isRecover) {
    mHelper.onYoutubePlayerChanged(youTubePlayer);
    // Switch youtube player
    mParent.mYoutubePlayer = youTubePlayer;
    youTubePlayer.setPlayerStateChangeListener(YoutubeViewHolder.this);
    youTubePlayer.setPlaybackEventListener(YoutubeViewHolder.this);
    // Force player style
    youTubePlayer.setPlayerStyle(
        getPlayerStyle() == CHROMELESS ? YouTubePlayer.PlayerStyle.CHROMELESS
            : YouTubePlayer.PlayerStyle.MINIMAL);
    if (!isRecover) {
      if (isSeeking) {
        isSeeking = false;
        youTubePlayer.loadVideo(getYoutubeVideoId(), seekPosition);
      } else {
        youTubePlayer.loadVideo(getYoutubeVideoId());
      }
      seekPosition = 0;
    }
  }

  @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
      YouTubeInitializationResult youTubeInitializationResult) {

  }

  @IntDef({
      CHROMELESS, MINIMUM
  }) @Retention(RetentionPolicy.SOURCE) public @interface PlayerStyle {
  }
}
