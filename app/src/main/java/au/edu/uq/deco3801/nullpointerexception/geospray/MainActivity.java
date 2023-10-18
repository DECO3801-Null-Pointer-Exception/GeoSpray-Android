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
import android.util.Log;
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
  private CreateOptionsFragment createOptionsFragment = new CreateOptionsFragment();
  private NavigationFragment navigationFragment = new NavigationFragment();
  private ImageGalleryFragment imageGalleryFragment = new ImageGalleryFragment();

  private FirebaseAuth mAuth;

//  private ProfileFragment profile;
  // TODO save profile information

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    mAuth = FirebaseAuth.getInstance();

//    mAuth.signOut(); //just for testing purposes
    //otherwise user stays signed in
    if (mAuth.getCurrentUser() == null) {
      mAuth.signInAnonymously();
    }

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    replaceFrag(imageGalleryFragment);

    binding.botnav.setOnItemSelectedListener(item -> {
      if (item.getItemId() == R.id.bot_create) {
        replaceFrag(createOptionsFragment);
      } else if (item.getItemId() == R.id.bot_navigation) {
        replaceFrag(navigationFragment);
      } else if (item.getItemId() == R.id.bot_home) {
        replaceFrag(imageGalleryFragment);
      } else if (item.getItemId() == R.id.bot_profile) {
//        setupProfile();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        createToast("User is:"+ currentUser); //todo


        if(currentUser != null) {
          if (!currentUser.isAnonymous()) {
            // user is not anonymous (signed in)
            Log.i("userinfo", currentUser.getDisplayName()+"");
            replaceFrag(new ProfileFragment());

          } else {
            //user is anonymous
            Intent login = new Intent(getApplicationContext(), UserLogin.class);
            startActivity(login);
          }
          // user a person
        } else {
          // user does not exist
          mAuth.signInAnonymously(); //create a user for them
          Intent login = new Intent(getApplicationContext(), UserLogin.class);
          startActivity(login);
        }
        //go to sign in page or login


      }
      return true;
    });
  }

  @Override
  public void onStart() {
    super.onStart();
//         Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
    if (currentUser != null && !currentUser.isAnonymous()) { //todo needed for toast to work with no email
//      createToast("User is Loaded");
//      createToast(currentUser.getDisplayName());
//      createToast(currentUser.getEmail());
      // TODO Testing user interaction
    } else {
      //set user to be anon
//      mAuth.signInAnonymously();

    }
  }
//todo
  public void setupProfile() {

  }

  public void replaceFrag(Fragment fragment) {
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction fmtrans = fm.beginTransaction().replace(R.id.frame_layout, fragment);
    fmtrans.addToBackStack(null);
    fmtrans.commit();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
  }

  public void createToast(String msg) {
    Toast.makeText(getApplicationContext(), (msg),
            Toast.LENGTH_SHORT).show();
  }

}
