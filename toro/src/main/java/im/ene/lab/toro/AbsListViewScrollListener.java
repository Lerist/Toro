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

import android.widget.AbsListView;

/**
 * Created by eneim on 1/31/16.
 *
 * @hide
 */
public class AbsListViewScrollListener implements AbsListView.OnScrollListener, ToroScrollHelper {

  protected final ToroManager mManager;

  public AbsListViewScrollListener(ToroManager manager) {
    this.mManager = manager;
  }

  @Override public void onScrollStateChanged(AbsListView view, int scrollState) {

  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                       int totalItemCount) {

  }

  @Override public ToroManager getManager() {
    return mManager;
  }
}
