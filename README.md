# Collector-5G
This Android Application is for retrieving and collecting information about a 5G device and its connectivity.
This tool has been developped for a semester project at EURECOM.
## Functionalities
It collects data about battery level and acceleration of the device, location of the user and connectivity parameters for 5G (SSRSRP, SSRSRQ, SSSINR). After collecting, the application parses the data into a JSON structures and send it to a broker.
## How to install and use our application
You must first set up your local Android development environment. This involves installing the Android SDK.
More info at https://developer.android.com/studio/index.html#downloads.
With the SDK setup complete, follow these steps to build the app:
1. Download the source code
2. Untar with tar -xvf android_source.tar.gz
3. cd to the android directory
4. Build the project using one of the following commands:
    - For a debug APK: ./gradlew assembleDebug
    - For a release APK: ./gradlew assembleRelease
    - For a release App Bundle: ./gradlew bundleRelease
5. The result can be found in:
    - Debug APK: app/build/outputs/apk/normal/debug/app-normal-debug.apk
    - Release APK: app/build/outputs/apk/normal/release/app-normal-release.apk
    - Release App Bundle: app/build/outputs/bundle/normalRelease/app-normal-release.aab
## Permissions
At launch time, the application is asking the user for two permissions : Location and Phone (It also uses Internet but it's not asked to the user). Please allow this permissions otherwise the application can't work. 
## Architecture
You can find the main  component of the application at /app/src/main/java/com/example/collector5g in this github.
This application is composed of two activity and an only one service :
- StartActivity is the launch activity (activity that open at launch time), it presents a form and a button. The form is designed to ask information about which broker the user want to share his personal data. So with the broker address, the form also asks for a potential username, password, a topic name and also a delay of collection (The broker collects every x seconds). The button start the connection with the broker and launch the second activity, MainActivity.
- MainActivity is the second activity, it presents a list of incolumn textViews and three buttons. The textViews are used to display the informations so the user knows the value of the data that are collected. The button start launch DataCollectingService, the button stop pause the service and the button disconnect stop the connection with the broker and redirect the user to StartActivity.
- DataCollectionService collect all the needed data with method getAllData. It diplays on the screen the information and sends it every x second to the broker.
