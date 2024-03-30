# GeoSpray-Android

Application that allows users to place virtual artwork on surfaces and share it with other users.

Currently based on Android.

Built with Android Studio, using ARCore and Firebase.

[![Watch the video](https://img.youtube.com/vi/AfgjNq2KmBU/hqdefault.jpg)](https://youtu.be/AfgjNq2KmBU?t=27)

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

## Development
To create the build enviroment you will need several keys and Android Studio

1) Download the Codebase
2) Install Android Studio
3) Open Android Studio and open the Codebase inside
4) Go to Tools -> Firebase
5) In the Firebase Assistant Page -> Realtime Database -> Get Started with Realtime Database [Java] -> Connect your app to Firebase
6) To connect with Firebase you will need permissions from the owners and an account permission
7) Add the Realtime Database SDK to your app
8) Repeat steps 4 - 7 for Cloud Storage for Firebase
9) Access the Build.gradle file and sync the grade file to update codebase
10) Get a googlemap API key and add it to local.properties ```GOOGLE_MAPS_API_KEY=API_KEY```, replacing ```API_KEY``` with the key

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
### Code
https://github.com/google-ar/codelab-cloud-anchors

https://github.com/googlecodelabs/maps-platform-101-android

https://firebase.google.com/docs/auth/android/password-auth

https://stackoverflow.com/questions/2986387/multi-line-edittext-with-done-action-button/41022589#41022589

https://youtu.be/4EKlAvjY74U

https://stackoverflow.com/questions/72436314/how-to-close-the-android-soft-keyboard

https://github.com/googlecodelabs/maps-platform-101-android

https://www.sportskeeda.com/roblox-news/100-unique-roblox-username-ideas-new-players-2022

https://stackoverflow.com/questions/31248257/androidgoogle-maps-get-the-address-of-location-on-touch

https://developer.android.com/develop/ui/views/components/menus

https://stackoverflow.com/questions/29664993/how-to-convert-dp-px-sp-among-each-other-especially-dp-and-sp

https://www.geeksforgeeks.org/how-to-create-a-paint-application-in-android/

https://stackoverflow.com/questions/22584244/how-to-generate-6-different-random-numbers-in-java

### Images
i1.png: https://pixabay.com/vectors/girl-sitting-clouds-moon-sky-mind-8301168/

i2.png: https://pixabay.com/vectors/egg-breakfast-fried-egg-7847875/

i3.png: https://pixabay.com/vectors/rubber-duck-yellow-4679464/

i4.jpg: https://pixabay.com/illustrations/man-dog-dogwalking-pet-relax-8070372/

i5.jpg: https://pixabay.com/illustrations/fish-capture-fish-catch-hands-7825240/

i6.png: https://pixabay.com/illustrations/milk-fresh-milk-drink-beverage-8029140/

i7.jpg: https://pixabay.com/photos/dandelion-flower-close-up-macro-8318529/

i8.jpg: https://pixabay.com/photos/dragon-fly-atrocalopteryx-atrata-8229773/

i9.jpg: https://pixabay.com/photos/spider-web-droplets-rain-drops-8159315/

i10.jpg: https://pixabay.com/photos/flower-floral-design-8140215/

i11.jpg: https://pixabay.com/photos/flower-rose-petals-flora-botany-7917595/

i12.jpg: https://pixabay.com/photos/white-blossom-flowers-tree-plant-8066657/

i13.jpg: https://pixabay.com/photos/living-room-quiet-houseplant-8254772/

i14.jpg: https://pixabay.com/photos/cat-animal-cat-portrait-cats-eyes-1045782/

i15.jpg: https://pixabay.com/photos/cat-face-eyes-portrait-1455468/

i16.jpg: https://pixabay.com/photos/lion-feline-big-cat-animal-safari-515028/

i17.jpg: https://pixabay.com/photos/pink-cherry-blossoms-flowers-branch-324175/

i18.jpg: https://pixabay.com/photos/dandelion-seeds-dew-dewdrops-445228/

i19.jpg: https://pixabay.com/photos/bubbles-water-bubbly-bubbling-230014/

i20.jpg: https://pixabay.com/photos/feather-ease-slightly-blue-airy-3010848/

i21.jpg: https://pixabay.com/photos/splashing-splash-aqua-water-165192/

i22.jpg: https://pixabay.com/photos/flowers-petals-dew-dewdrops-174817/

i23.jpg: https://pixabay.com/photos/leaves-maple-autumn-fall-foliage-57427/

i24.jpg: https://pixabay.com/photos/forest-fall-cup-motto-gloves-4656856/

i25.jpg: https://pixabay.com/photos/olive-oil-olives-food-oil-natural-968657/

i26.jpg: https://pixabay.com/photos/oranges-citrus-fruits-fruits-1995056/

i27.jpg: https://pixabay.com/photos/pomegranate-fruits-food-sliced-3383814/

i28.jpg: https://pixabay.com/photos/pancakes-schaumomelette-omelette-1984716/

i29.jpg: https://pixabay.com/photos/orion-nebula-emission-nebula-11107/

i30.jpg: https://pixabay.com/photos/earth-globe-planet-world-space-11015/

i31.jpg: https://pixabay.com/photos/moon-sky-luna-craters-lunar-1527501/

i32.jpg: https://pixabay.com/photos/star-trails-night-stars-rotation-1846734/

i33.jpg: https://pixabay.com/illustrations/cactus-plants-pots-flowers-pattern-2191647/

i34.png: https://pixabay.com/illustrations/art-space-planet-earth-mountain-2907939/

i35.jpg: https://pixabay.com/illustrations/flowers-bouquet-floral-arrangement-5548043/

profile_bg.jpg: https://pixabay.com/photos/astronomy-bright-constellation-dark-1867616/
