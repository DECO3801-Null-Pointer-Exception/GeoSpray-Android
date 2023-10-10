/*
 * Copyright 2019 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import au.edu.uq.deco3801.nullpointerexception.geospray.databinding.ActivityMainBinding;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FullScreenHelper;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.gallery_adapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.gallery_image;

/**
 * Main Activity for the Cloud Anchors Codelab.
 *
 * <p>The bulk of the logic resides in the {@link CloudAnchorFragment}. The activity only creates
 * the fragment and attaches it to this Activity.
 */
public class MainActivity extends AppCompatActivity {
  ActivityMainBinding binding;
  RecyclerView recyclerView;
  gallery_adapter adapter;
  boolean isLoading = false;
  ArrayList<gallery_image> images = new ArrayList<>();
  ArrayList<ArrayList<gallery_image>> gallery_images = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    replaceFrag(new ImageGalleryFragment());

    binding.botnav.setOnItemSelectedListener(item -> {
      if (item.getItemId() == R.id.bot_create) {
        replaceFrag(new CreateOptionsFragment());
      } else if (item.getItemId() == R.id.bot_navigation) {
        replaceFrag(new NavigationFragment());
      } else if (item.getItemId() == R.id.bot_home) {
        replaceFrag(new ImageGalleryFragment());
      }
      return true;
    });

//    SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_gallery);
//    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//      @Override
//      public void onRefresh() {
//        refreshLayout.setRefreshing(false);
//      }
//    });

//    recyclerView = findViewById(R.id.gallery_recycler);
//    setupGallery();
//    adapter = new gallery_adapter(this, gallery_images);
//    recyclerView.setAdapter(adapter);
//    recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//      @Override
//      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//        super.onScrollStateChanged(recyclerView, newState);
//        if (!recyclerView.canScrollVertically(1)) {
//          gallery_images.add(images);
//          adapter.notifyDataSetChanged();
//          Log.i("POSITION", "END");
//        }
//      }
//    });
  }

  protected void replaceFrag(Fragment fragment) {
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction fmtrans = fm.beginTransaction().replace(R.id.frame_layout, fragment);
    fmtrans.commit();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
  }

//  public void setupGallery() {
//
//    for (int i = 0; i < 16; i++) {
//      images.add(new gallery_image(i, BitmapFactory.decodeResource(getResources(), R.drawable.logo)));
//    }
//    gallery_images.add(images);
//  }
}
