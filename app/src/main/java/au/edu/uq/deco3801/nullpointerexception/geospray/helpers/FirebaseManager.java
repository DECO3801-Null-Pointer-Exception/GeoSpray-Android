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

/**
 * Helper class for Firebase storage of cloud anchor IDs.
 * <br/>
 * Code adapted from Google's Cloud Anchors Codelab, available at:
 * <a href="https://github.com/google-ar/codelab-cloud-anchors">
 *     https://github.com/google-ar/codelab-cloud-anchors</a>
 * <br/>
 * Modified by: Raymond Dufty, Xingyun Wang, Esmond Wu.
 * <br/>
 * Modifications include: adding additional fields to the database.
 */
public class FirebaseManager {

  /** Listener for a new Cloud Anchor ID from the Firebase Database. */
  public interface CloudAnchorIdListener {
    void onCloudAnchorIdAvailable(String cloudAnchorId);
  }

  /** Listener for a new image rotation from the Firebase Database. */
  public interface ImageRotationListener {
    void onImageRotationAvailable(Integer rotation);
  }

    /** Listener for a new image scale from the Firebase Database. */
  public interface ImageScaleListener {
    void onImageScaleAvailable(Float scale);
  }

    /** Listener for a new image title from the Firebase Database. */
    public interface ImageTitleListener {
        void onImageTitleAvailable(String title);
    }

    /** Listener for a new image description from the Firebase Database. */
    public interface ImageDescriptionListener {
        void onImageDescriptionAvailable(String description);
    }

    /** Listener for a new image date from the Firebase Database. */
    public interface ImageDateListener {
        void onImageDateAvailable(String date);
    }

    /** Listener for a new user ID from the Firebase Database. */
    public interface ImageUidListener {
        void onImageUidAvailable(String uid);
    }

    /** Listener for a new latitude from the Firebase Database. */
    public interface ImageLatListener {
        void onImageLatAvailable(Double lat);
    }

    /** Listener for a new longitude from the Firebase Database. */
    public interface ImageLongListener {
        void onImageLongAvailable(Double longitude);
    }

    /** Listener for a new like from the Firebase Database. */
    public interface ImageLikedListener {
        void onImageLikedAvailable(Boolean liked);
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

  /** Stores the image and associated data in the configured Firebase Database. */
  public void storeUsingShortCode(int shortCode, String cloudAnchorId, int rotation, float scale, double latitude, double longitude, String title, String description, String date, String uid) {
    rootRef.child("" + shortCode).child("anchor").setValue(cloudAnchorId);
    rootRef.child("" + shortCode).child("rotation").setValue(rotation);
    rootRef.child("" + shortCode).child("scale").setValue(scale);
    rootRef.child("" + shortCode).child("lat").setValue(latitude);
    rootRef.child("" + shortCode).child("long").setValue(longitude);
    rootRef.child("" + shortCode).child("title").setValue(title);
    rootRef.child("" + shortCode).child("description").setValue(description);
    rootRef.child("" + shortCode).child("date").setValue(date);
    rootRef.child("" + shortCode).child("uid").setValue(uid);
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

    /**
     * Retrieves the image rotation using a short code. Returns an empty string if an image rotation
     * was not stored for this short code.
     */
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

    /**
     * Retrieves the UID using a short code. Returns an empty string if a UID
     * was not stored for this short code.
     */
    public void getImageUid(int shortCode, ImageUidListener listener) {
        rootRef
                .child("" + shortCode)
                .child("uid")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageUidAvailable(dataSnapshot.getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageUid was cancelled.",
                                        error.toException());
                                listener.onImageUidAvailable(null);
                            }
                        });
    }

    /**
     * Retrieves the image scale using a short code. Returns an empty string if an image scale
     * was not stored for this short code.
     */
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

    /**
     * Retrieves the image title using a short code. Returns an empty string if an image title
     * was not stored for this short code.
     */
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

    /**
     * Retrieves the image description using a short code. Returns an empty string if an image description
     * was not stored for this short code.
     */
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

    /**
     * Retrieves the image date using a short code. Returns an empty string if an image date
     * was not stored for this short code.
     */
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

    /**
     * Retrieves the image latitude using a short code. Returns an empty string if a latitude
     * was not stored for this short code.
     */
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
                                        "The Firebase operation for getImageLat was cancelled.",
                                        error.toException());
                                listener.onImageLatAvailable(null);
                            }
                        });
    }

    /**
     * Retrieves the image longitude using a short code. Returns an empty string if a longitude
     * was not stored for this short code.
     */
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
                                        "The Firebase operation for getImageLong was cancelled.",
                                        error.toException());
                                listener.onImageLongAvailable(null);
                            }
                        });
    }

    /**
     * Retrieves the image like status using a short code. Returns an empty string if a like
     * was not stored for this short code.
     */
    public void getImageLiked(int shortCode, ImageLikedListener listener) {
        rootRef
                .child("" + shortCode)
                .child("liked")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Listener invoked when the data is successfully read from Firebase.
                                listener.onImageLikedAvailable(dataSnapshot.getValue(Boolean.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(
                                        TAG,
                                        "The Firebase operation for getImageLiked was cancelled.",
                                        error.toException());
                                listener.onImageLikedAvailable(null);
                            }
                        });
    }

    /**
     * Returns a reference to the root of the database.
     *
     * @return The database root.
     */
    public DatabaseReference getRootRef() {
        return rootRef;
    }
}
