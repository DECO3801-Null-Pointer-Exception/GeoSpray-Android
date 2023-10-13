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

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import au.edu.uq.deco3801.nullpointerexception.geospray.databinding.ActivityMainBinding;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.CloudAnchorFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.CreateOptionsFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.ImageGalleryFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.NavigationFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.PreviewFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.ProfileFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FullScreenHelper;

/**
 * Main Activity for the Cloud Anchors Codelab.
 *
 * <p>The bulk of the logic resides in the {@link CloudAnchorFragment}. The activity only creates
 * the fragment and attaches it to this Activity.
 */
public class MainActivity extends AppCompatActivity {
  private ActivityMainBinding binding;

  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    replaceFrag(new ImageGalleryFragment());

    mAuth = FirebaseAuth.getInstance();

    binding.botnav.setOnItemSelectedListener(item -> {
      if (item.getItemId() == R.id.bot_create) {
        replaceFrag(new CreateOptionsFragment());
      } else if (item.getItemId() == R.id.bot_navigation) {
        replaceFrag(new NavigationFragment());
      } else if (item.getItemId() == R.id.bot_home) {
        replaceFrag(new ImageGalleryFragment());
      } else if (item.getItemId() == R.id.bot_profile) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toast.makeText(getApplicationContext(), ("User is:"+ currentUser),
                Toast.LENGTH_SHORT).show();
        if(currentUser != null){
          replaceFrag(new ProfileFragment());
        } else {
          Intent login = new Intent(getApplicationContext(), UserLogin.class);
          startActivity(login);
        }

      } else if (item.getItemId() == R.id.bot_search) {
        replaceFrag(new PreviewFragment());
      }
      return true;
    });
  }


  public void replaceFrag(Fragment fragment) {
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
