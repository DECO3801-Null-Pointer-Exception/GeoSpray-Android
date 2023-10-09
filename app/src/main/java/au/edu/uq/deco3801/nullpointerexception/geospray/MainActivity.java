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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import au.edu.uq.deco3801.nullpointerexception.geospray.databinding.ActivityMainBinding;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FullScreenHelper;

/**
 * Main Activity for the Cloud Anchors Codelab.
 *
 * <p>The bulk of the logic resides in the {@link CloudAnchorFragment}. The activity only creates
 * the fragment and attaches it to this Activity.
 */
public class MainActivity extends AppCompatActivity {
  ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    binding.botnav.setOnItemSelectedListener(item -> {
      if (item.getItemId() == R.id.bot_create) {
        replaceFrag(new CloudAnchorFragment());
      } else if (item.getItemId() == R.id.bot_navigation) {
        replaceFrag(new NavigationFragment());
      }
      return true;
    });

//    FragmentManager fm = getSupportFragmentManager();
//    Fragment frag = fm.findFragmentById(R.id.fragment_container);
//    if (frag == null) {
//      frag = new CloudAnchorFragment();
//      fm.beginTransaction().replace(R.id.fragment_container, frag).commit();
//      fm.beginTransaction().commit();
//    }

//    Button goto_ar = (Button) findViewById(R.id.gotoar);
//    goto_ar.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment frag = fm.findFragmentById(R.id.fragment_container);
//        if (frag == null) {
//          frag = new CloudAnchorFragment();
//          fm.beginTransaction().add(R.id.fragment_container, frag).commit();
//        }
//      }
//    });
  }

  private void replaceFrag(Fragment fragment) {
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction fmtrans = fm.beginTransaction().replace(R.id.frame_layout, fragment);
    fmtrans.commit();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
  }
}
