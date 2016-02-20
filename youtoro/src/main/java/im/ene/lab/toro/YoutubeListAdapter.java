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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by eneim on 2/15/16.
 *
 * A Custom Adapter which support Youtube playback on its children.
 */
public abstract class YoutubeListAdapter extends ToroAdapter<YoutubeViewHolder>
    implements VideoPlayerManager, Handler.Callback {

  /**
   * To support {@link YouTubePlayerSupportFragment}
   */
  public final FragmentManager mFragmentManager;
  private final ConcurrentHashMap<String, Integer> mVideoStates = new ConcurrentHashMap<>();
  private final Handler mHandler = new Handler(Looper.getMainLooper(), this);
  private final int MESSAGE_PROGRESS = 4;
  YouTubePlayer mYoutubePlayer;
  // Current player widget
  private YoutubeViewHolder mPlayer;

  public YoutubeListAdapter(FragmentManager fragmentManager) {
    super();
    this.mFragmentManager = fragmentManager;
  }

  /**
   * @return latest Video Player
   */
  @Override public ToroPlayer getPlayer() {
    return mPlayer;
  }

  /**
   * Set current video player. There would be at most one Video player at a time.
   *
   * @param player the current Video Player of this manager
   */
  @Override public void setPlayer(ToroPlayer player) {
    if (!(player instanceof YoutubeViewHolder)) {
      throw new IllegalArgumentException("This manager accepts only YoutubeViewHolder");
    }

    mPlayer = (YoutubeViewHolder) player;
  }

  @Override public void onRegistered() {

  }

  @Override public void onUnregistered() {
    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
    }
  }

  /**
   * Start playing current video
   */
  @Override public void startPlayback() {
    if (mPlayer != null && !mPlayer.isStarting()) {
      mPlayer.start();

      if (mHandler != null) {
        // Remove old callback if exist
        mHandler.removeMessages(MESSAGE_PROGRESS);
        mHandler.sendEmptyMessageDelayed(MESSAGE_PROGRESS, 250);
      }
    }
  }

  /**
   * Pause current video
   */
  @Override public void pausePlayback() {
    if (mHandler != null) {
      mHandler.removeMessages(MESSAGE_PROGRESS);
    }

    if (mPlayer != null) {
      mPlayer.pause();
    }
  }

  /**
   * Save current video state
   */
  @Override public void saveVideoState(String videoId, @Nullable Integer position, long duration) {
    if (videoId != null) {
      mVideoStates.put(videoId, position == null ? Integer.valueOf(0) : position);
    }
  }

  /**
   * Restore and setup state of a Video to current video player
   */
  @Override public void restoreVideoState(String videoId) {
    if (mPlayer == null) {
      return;
    }

    Integer position = mVideoStates.get(videoId);
    if (position == null) {
      position = 0;
    }

    // See {@link android.media.MediaPlayer#seekTo(int)}
    if (mPlayer != null) {
      mPlayer.seekTo(position);
    }
  }

  @Override public boolean handleMessage(Message msg) {
    switch (msg.what) {
      case MESSAGE_PROGRESS:
        if (mPlayer != null) {
          mPlayer.onPlaybackProgress(mPlayer.getCurrentPosition(), mPlayer.getDuration());
        }
        mHandler.removeMessages(MESSAGE_PROGRESS);
        mHandler.sendEmptyMessageDelayed(MESSAGE_PROGRESS, 250);
        return true;
      default:
        return false;
    }
  }
}
