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

package im.ene.lab.toro.sample.viewholder;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import im.ene.lab.toro.AbsVideoViewHolder;
import im.ene.lab.toro.sample.R;
import im.ene.lab.toro.sample.data.SimpleVideoObject;
import im.ene.lab.toro.widget.TextureVideoView;

/**
 * Created by eneim on 2/1/16.
 */
public class SampleListViewHolder extends AbsVideoViewHolder implements Handler.Callback {

  private static final String TAG = "VideoViewHolder";

  private static final int MESSAGE_PLAYER_START = 1;

  private static final int MESSAGE_PLAYER_PAUSE = 1 << 1;

  private static final int MESSAGE_SEEK = 1 << 2;

  private static final int MESSAGE_DELAY = 200;

  public static final int LAYOUT_RES = R.layout.vh_texture_video;

  private Handler mHandler = new Handler(this);
  private boolean mIsVideoPathSet = false;

  private TextureVideoView mVideoView;
  private SimpleVideoObject mItem;

  public SampleListViewHolder(View itemView) {
    super(itemView);
    mVideoView = (TextureVideoView) itemView.findViewById(R.id.video);

    // mVideoView.setOnPreparedListener(this);
    // mVideoView.setOnCompletionListener(this);
    // mVideoView.setOnErrorListener(this);
    // mVideoView.setOnInfoListener(this);
    // mVideoView.setOnSeekCompleteListener(this);
  }

  public void bind(Object item) {
    if (!(item instanceof SimpleVideoObject)) {
      throw new IllegalStateException("Unexpected object: " + item.toString());
    }

    mItem = (SimpleVideoObject) item;
    Log.d(TAG, "bind() called with: " + "item = [" + item + "]");
    // mCurrentState = State.STATE_IDLE;
    mIsVideoPathSet = false;
    mVideoView.setVideoPath(((SimpleVideoObject) item).video);
    mIsVideoPathSet = true;
  }

  @Override public void start() {
    mHandler.sendEmptyMessageDelayed(MESSAGE_PLAYER_START, MESSAGE_DELAY);
  }

  @Override public void pause() {
    mHandler.sendEmptyMessageDelayed(MESSAGE_PLAYER_PAUSE, MESSAGE_DELAY);
  }

  @Override public void seekTo(int pos) {
    // mCurrentState = State.STATE_SEEKING;
    mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_SEEK, pos, 0), MESSAGE_DELAY);
  }

  @Override public boolean wantsToPlay(Rect parentRect, @NonNull Rect childRect) {
    int visibleHeight = childRect.bottom - childRect.top;
    return visibleHeight > itemView.getHeight() * 0.7;
  }

  @Override public float visibleAreaOffset() {
    Rect videoRect = new Rect();
    mVideoView.getLocalVisibleRect(videoRect);
    return mVideoView.getHeight() <= 0 ? 1.f : videoRect.height() / (float) mVideoView.getHeight();
  }

  @Nullable @Override public Long getVideoId() {
    return (long) mItem.hashCode();
  }

  @Override public void onActivityPaused() {
    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
    }
    mHandler = null;
  }

  @Override public void onActivityResumed() {
    mHandler = new Handler(this);
  }

  @Override public boolean handleMessage(Message msg) {
    switch (msg.what) {
      case MESSAGE_PLAYER_START:
        if (mVideoView != null) {
          mVideoView.start();
        } else {
          // View is unavailable, re-send the message to wait for it
          mHandler.removeMessages(msg.what);
          mHandler.sendEmptyMessageDelayed(msg.what, MESSAGE_DELAY);
        }
        return true;
      case MESSAGE_PLAYER_PAUSE:
        if (mVideoView != null) {
          mVideoView.pause();
        }
        return true;
      case MESSAGE_SEEK:
        if (mVideoView != null && mIsVideoPathSet) {
          mVideoView.seekTo(msg.arg1);
        } else {
          mHandler.removeMessages(MESSAGE_SEEK);
          mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_SEEK, msg.arg1, 0),
              MESSAGE_DELAY);
        }
        return true;
      default:
        return false;
    }
  }
}
