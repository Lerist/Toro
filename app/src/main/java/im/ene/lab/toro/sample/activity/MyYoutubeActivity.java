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

package im.ene.lab.toro.sample.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import im.ene.lab.toro.YoutubeListAdapter;
import im.ene.lab.toro.YoutubeViewHolder;
import im.ene.lab.toro.sample.BuildConfig;
import im.ene.lab.toro.sample.R;
import im.ene.lab.toro.sample.data.SimpleVideoObject;
import im.ene.lab.toro.sample.data.VideoSource;
import im.ene.lab.toro.sample.fragment.RecyclerViewFragment;
import im.ene.lab.toro.sample.util.Util;

/**
 * Created by eneim on 2/12/16.
 */
public class MyYoutubeActivity extends AppCompatActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    getLayoutInflater().setFactory(this);
    super.onCreate(savedInstanceState);
    if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(android.R.id.content, YoutubeListFragment.newInstance())
          .commit();
    }
  }

  public static class YoutubeListFragment extends RecyclerViewFragment {

    public static final String TAG = "YoutubeListFragment";

    public static YoutubeListFragment newInstance() {
      return new YoutubeListFragment();
    }

    @NonNull @Override protected RecyclerView.LayoutManager getLayoutManager() {
      return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @NonNull @Override protected RecyclerView.Adapter getAdapter() {
      return new Adapter(getChildFragmentManager());
    }
  }

  private static class Adapter extends YoutubeListAdapter {

    public Adapter(FragmentManager fragmentManager) {
      super(fragmentManager);
    }

    @Nullable @Override protected Object getItem(int position) {
      return new SimpleVideoObject(VideoSource.YOUTUBES[position % VideoSource.YOUTUBES.length]);
    }

    @Override
    public MyYoutubeActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(MyYoutubeActivity.ViewHolder.LAYOUT_RES, parent, false);
      return new MyYoutubeActivity.ViewHolder(this, view);
    }

    @Override public int getItemCount() {
      return VideoSource.YOUTUBES.length * 10;
    }
  }

  static class ViewHolder extends YoutubeViewHolder
      implements YouTubeThumbnailView.OnInitializedListener {

    private static final int LAYOUT_RES = R.layout.vh_youtube_video;

    @Bind(R.id.thumbnail) YouTubeThumbnailView mThumbnail;
    @Bind(R.id.video_id) TextView mVideoId;
    @Bind(R.id.info) TextView mInfo;
    @Bind(R.id.container) FrameLayout mContainer;
    private SimpleVideoObject mItem;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ViewHolder(Adapter adapter, View itemView) {
      super(itemView, adapter);
      TAG = toString();
      ButterKnife.bind(this, itemView);
      // Must set this
      View view = mContainer.getChildAt(0);
      if (view != null) {
        view.setId(mFragmentId);
      }
    }

    @Nullable @Override public String getVideoId() {
      return mItem.video + " - " + getAdapterPosition();  // holds uniqueness in Adapter
    }

    @NonNull @Override public View getVideoView() {
      View view = mYoutubeFragment == null ? mContainer.findViewById(mFragmentId)
          : mYoutubeFragment.getView();
      return view != null ? view : mContainer.findViewById(mFragmentId);
    }

    @Override public void onPlaybackStarted() {
      super.onPlaybackStarted();
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.INVISIBLE);
      }
      mInfo.setText("Started");
    }

    @Override public String getYoutubeVideoId() {
      return mItem != null ? mItem.video : null;
    }

    @Override public void bind(@Nullable Object object) {
      if (object == null || !(object instanceof SimpleVideoObject)) {
        throw new IllegalArgumentException("Illegal");
      }

      mItem = (SimpleVideoObject) object;
      mVideoId.setText(mItem.video);
    }

    @Override public void onViewHolderBound() {
      super.onViewHolderBound();
      mInfo.setText("Bound");
      mThumbnail.initialize(BuildConfig.YOUTUBE_API_KEY, this);
    }

    @Override public void onPlaybackProgress(int position, int duration) {
      super.onPlaybackProgress(position, duration);
      mInfo.setText(Util.timeStamp(position, duration));
    }

    @Override public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView,
        YouTubeThumbnailLoader youTubeThumbnailLoader) {
      youTubeThumbnailLoader.setVideo(mItem.video);
    }

    @Override public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView,
        YouTubeInitializationResult youTubeInitializationResult) {

    }

    private final String TAG;

    @Override public void onLoading() {
      super.onLoading();
      Log.d(TAG, "onLoading() called with: " + "");
      mInfo.setText("Loading");
    }

    @Override public void onLoaded(String s) {
      super.onLoaded(s);
      mInfo.setText("Loaded");
    }

    @Override public void onError(YouTubePlayer.ErrorReason errorReason) {
      super.onError(errorReason);
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.VISIBLE);
      }
      mInfo.setText("Error:" + errorReason.name());
    }

    @Override public void onPlaybackPaused() {
      super.onPlaybackPaused();
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.VISIBLE);
      }
      mInfo.setText("Paused");
    }

    @Override public void onPlaybackStopped() {
      super.onPlaybackStopped();
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.VISIBLE);
      }
      mInfo.setText("Stopped");
    }

    @Override public String toString() {
      return Integer.toHexString(hashCode()) + " position=" + getAdapterPosition();
    }
  }
}
