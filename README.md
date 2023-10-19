# GeoSpray-Android

Application that allows users to place virtual artwork on surfaces and share it with other users.

Currently based on Android.

Built with Android Studio, using the ARCore Geospatial API and Firebase.

## Project Outline
The project involves developing an interactive Augmented Reality (AR) experience. This experience will utilise virtual “graffiti” that users can create using an in-built interface. This digital art can be placed on flat surfaces (for example, a wall) and viewed by passersby through the application, seamlessly blending the virtual and real worlds. Users can geo-tag their creations to specific locations, enabling a contextual connection between art and place. The application fosters community engagement, allowing users to discover, rate and appreciate each other’s virtual graffiti. This will create a dynamic platform that merges artistic expression with AR technology. 

# Compiling

## Build
To run the application, download the Apk file onto your android device.

Then, find it in your file manager and run file.

If it asks for permission to run apk files from the file manager, accept and enable it in the settings.

Then when it asks to install, press "install"
it will say "app is not authorised" click on the link under it and press "install anyways"

Once this is done, you will have the app install on your phone

1) Download the latest APK file named Geospray onto your Android Device. This can be done through USB transfer from the codebase.
2) Find the Application on your phone storage and Run the Apk file
3) If this is your first time installing an apk file, you may need to enable it in your settings. Go to "Install unknown apps" and enable for your file manager
4) Install the Application

   
## Developement
To create the build enviroment you will need several keys and Android Studio

1) Download the Codebase
2) Install Android Studio
3) Open Android Studio and open the Codebase inside
4) Go to Tools -> Firebase
5) In the Firebase Assistant Page -> Realtime Database -> Get Started with Realtime Database -> Connect your app with Firebase
6) To connect with Firebase you will need permissions from the owners and an account permission
7) Access the Build.gradle file and sync the grade file to update codebase
8) Get a googlemap API key and add it to local.properties ```GOOGLE_MAPS_API_KEY={API_KEY}```


### Run the app on your phone
1) Enable Developer Options on your android device
2) Enable USB Debugging
3) Plug in your device to the computer running Android Studio
4) Accept permission when asked to be connected
5) Go to top right and choose your device
6) Run the application installer

(Note: You may need permissions to access the API for ARCore)


### Build the Signed APK File from Android Studio
1) Build -> Generate Signed Bundle/APK... -> APK
2) Choose the GeoSprayKey.jks file and passwords (Note, you may need to ask the owners for access to the build key and credentials)
3) Choose Build Variant -> Release
4) Locate File with APK
   

# References



