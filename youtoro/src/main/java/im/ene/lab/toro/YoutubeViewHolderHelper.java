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

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import com.google.android.youtube.player.YouTubePlayer;

/**
 * Created by eneim on 2/15/16.
 */
final class YoutubeViewHolderHelper extends BaseViewHolderHelper {

  private static final String TAG = "YoutubeViewHolderHelper";

  YoutubeViewHolderHelper() {

  }

  @Override public void onAttachedToParent(ToroPlayer player, View itemView, ViewParent parent) {

  }

  @Override public void onDetachedFromParent(ToroPlayer player, View itemView, ViewParent parent) {

  }

  @Override public boolean onItemLongClick(ToroPlayer player, View itemView, ViewParent parent) {
    return false;
  }

  @Override public void onPrepared(ToroPlayer player, View itemView, ViewParent parent,
      MediaPlayer mediaPlayer) {
  }

  // From YoutubePlayer
  public void onPlaying() {
    Log.d(TAG, "onPlaying() called with: " + "");
  }

  public void onPaused() {
    Log.d(TAG, "onPaused() called with: " + "");
  }

  public void onStopped() {
    Log.d(TAG, "onStopped() called with: " + "");
  }

  public void onBuffering(boolean b) {
    Log.d(TAG, "onBuffering() called with: " + "b = [" + b + "]");
  }

  public void onSeekTo(int i) {
    Log.d(TAG, "onSeekTo() called with: " + "i = [" + i + "]");
  }

  public void onLoading() {
    Log.d(TAG, "onLoading() called with: " + "");
  }

  public void onLoaded(String s) {
    Log.d(TAG, "onLoaded() called with: " + "s = [" + s + "]");
  }

  public void onAdStarted() {
    Log.d(TAG, "onAdStarted() called with: " + "");
  }

  public void onVideoStarted(ToroPlayer player) {
    player.onPlaybackStarted();
  }

  public void onVideoEnded(ToroPlayer player) {
    player.onPlaybackStopped();
  }

  public void onError(ToroPlayer player, YouTubePlayer.ErrorReason errorReason) {
    player.onPlaybackError(null, 0, 0);
  }

  public void onYoutubePlayerChanged(YouTubePlayer newPlayer) {
    Log.e(TAG, "onYoutubePlayerChanged() called with: " + "newPlayer = [" + newPlayer + "]");
  }
}
