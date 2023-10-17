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

package au.edu.uq.deco3801.nullpointerexception.geospray.helpers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

/** Helper class for Firebase storage of cloud anchor IDs. */
public class FirebaseManager {

  /** Listener for a new Cloud Anchor ID from the Firebase Database. */
  public interface CloudAnchorIdListener {
    void onCloudAnchorIdAvailable(String cloudAnchorId);
  }

  public interface ImageRotationListener {
    void onImageRotationAvailable(Integer rotation);
  }

  public interface ImageScaleListener {
    void onImageScaleAvailable(Float scale);
  }

    public interface ImageTitleListener {
        void onImageTitleAvailable(String title);
    }

    public interface ImageDescriptionListener {
        void onImageDescriptionAvailable(String description);
    }

    public interface ImageDateListener {
        void onImageDateAvailable(String date);
    }

    public interface UserUploadedListener {
        void onUserUploadedAvailable(Integer userUploaded);
    }

    public interface ImageLatListener {
        void onImageLatAvailable(Double lat);
    }

    public interface ImageLongListener {
        void onImageLongAvailable(Double longitude);
    }

  /** Listener for a new short code from the Firebase Database. */
  public interface ShortCodeListener {
    void onShortCodeAvailable(Integer shortCode);
  }

  private static final String TAG = FirebaseManager.class.getName();
  private static final String KEY_ROOT_DIR = "images";
  private static final String KEY_NEXT_SHORT_CODE = "next_short_code";
  private static final int INITIAL_SHORT_CODE = 1;
  private final DatabaseReference rootRef;

  /** Constructor that initializes the Firebase connection. */
  public FirebaseManager(Context context) {
    FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
    rootRef = FirebaseDatabase.getInstance(firebaseApp).getReference().child(KEY_ROOT_DIR);
    DatabaseReference.goOnline();
  }

  /** Gets a new short code that can be used to store the anchor ID. */
  public void nextShortCode(ShortCodeListener listener) {
    // Run a transaction on the node containing the next short code available. This increments the
    // value in the database and retrieves it in one atomic all-or-nothing operation.
    rootRef
        .child(KEY_NEXT_SHORT_CODE)
        .runTransaction(
            new Transaction.Handler() {
              @NonNull
              @Override
              public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer shortCode = currentData.getValue(Integer.class);
                if (shortCode == null) {
                  // Set the initial short code if one did not exist before.
                  shortCode = INITIAL_SHORT_CODE - 1;
                }
                currentData.setValue(shortCode + 1);
                return Transaction.success(currentData);
              }

              @Override
              public void onComplete(
                  DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (!committed) {
                  Log.e(TAG, "Firebase Error", error.toException());
                  listener.onShortCodeAvailable(null);
                } else {
                  listener.onShortCodeAvailable(currentData.getValue(Integer.class));
                }
              }
            });
  }

  /** Stores the cloud anchor ID in the configured Firebase Database. */
  public void storeUsingShortCode(int shortCode, String cloudAnchorId, int rotation, float scale, double latitude, double longitude, String title, String description, String date, int userUploaded) {
    rootRef.child("" + shortCode).child("anchor").setValue(cloudAnchorId);
    rootRef.child("" + shortCode).child("rotation").setValue(rotation);
    rootRef.child("" + shortCode).child("scale").setValue(scale);
    rootRef.child("" + shortCode).child("lat").setValue(latitude);
    rootRef.child("" + shortCode).child("long").setValue(longitude);
    rootRef.child("" + shortCode).child("title").setValue(title);
    rootRef.child("" + shortCode).child("description").setValue(description);
    rootRef.child("" + shortCode).child("date").setValue(date);
    rootRef.child("" + shortCode).child("userUploaded").setValue(userUploaded); // todo
  }

  /**
   * Retrieves the cloud anchor ID using a short code. Returns an empty string if a cloud anchor ID
   * was not stored for this short code.
   */
  public void getCloudAnchorId(int shortCode, CloudAnchorIdListener listener) {
    rootRef
        .child("" + shortCode)
        .child("anchor")
        .addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Listener invoked when the data is successfully read from Firebase.
                listener.onCloudAnchorIdAvailable(String.valueOf(dataSnapshot.getValue()));
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(
                    TAG,
                    "The Firebase operation for getCloudAnchorId was cancelled.",
                    error.toException());
                listener.onCloudAnchorIdAvailable(null);
              }
            });
  }

    public void getImageRotation(int shortCode, ImageRotationListener listener) {
        rootRef
                .child("" + shortCode)
                .child("rotation")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageRotationAvailable(dataSnapshot.getValue(Integer.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageRotation was cancelled.",
                                        error.toException());
                                listener.onImageRotationAvailable(null);
                            }
                        });
    }

    public void getImageUid(int shortCode, ImageRotationListener listener) {
        rootRef
                .child("" + shortCode)
                .child("uid")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageRotationAvailable(dataSnapshot.getValue(Integer.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageRotation was cancelled.",
                                        error.toException());
                                listener.onImageRotationAvailable(null);
                            }
                        });
    }

    public void getImageScale(int shortCode, ImageScaleListener listener) {
        rootRef
                .child("" + shortCode)
                .child("scale")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageScaleAvailable(dataSnapshot.getValue(Float.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageScale was cancelled.",
                                        error.toException());
                                listener.onImageScaleAvailable(null);
                            }
                        });
    }

    public void getImageTitle(int shortCode, ImageTitleListener listener) {
        rootRef
                .child("" + shortCode)
                .child("title")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageTitleAvailable(dataSnapshot.getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageTitle was cancelled.",
                                        error.toException());
                                listener.onImageTitleAvailable(null);
                            }
                        });
    }

    public void getImageDescription(int shortCode, ImageDescriptionListener listener) {
        rootRef
                .child("" + shortCode)
                .child("description")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageDescriptionAvailable(dataSnapshot.getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageDescription was cancelled.",
                                        error.toException());
                                listener.onImageDescriptionAvailable(null);
                            }
                        });
    }

    public void getImageDate(int shortCode, ImageDateListener listener) {
        rootRef
                .child("" + shortCode)
                .child("date")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageDateAvailable(dataSnapshot.getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageDate was cancelled.",
                                        error.toException());
                                listener.onImageDateAvailable(null);
                            }
                        });
    }

    public void getUserUploaded(int shortCode, UserUploadedListener listener) {
        rootRef
                .child("" + shortCode)
                .child("userUploaded")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onUserUploadedAvailable(dataSnapshot.getValue(Integer.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageDate was cancelled.",
                                        error.toException());
                                listener.onUserUploadedAvailable(null);
                            }
                        });
    }

    public void getImageLat(int shortCode, ImageLatListener listener) {
        rootRef
                .child("" + shortCode)
                .child("lat")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageLatAvailable(dataSnapshot.getValue(Double.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageDate was cancelled.",
                                        error.toException());
                                listener.onImageLatAvailable(null);
                            }
                        });
    }

    public void getImageLong(int shortCode, ImageLongListener listener) {
        rootRef
                .child("" + shortCode)
                .child("long")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageLongAvailable(dataSnapshot.getValue(Double.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageDate was cancelled.",
                                        error.toException());
                                listener.onImageLongAvailable(null);
                            }
                        });
    }

    public DatabaseReference getRootRef() {
        return rootRef;
    }
}
