# ApkExtractor
A simplify Apk extractor，build with React Native

Dependencies：[react-native-tab-view](https://github.com/react-native-community/react-native-tab-view)

![demo.gif](https://github.com/zjkhiyori/ApkExtractorRN/blob/master/example/demo.gif)

## Quick Start
### Prerequisites
* Installed [Yarn](https://yarnpkg.com/)
* [React Native](https://facebook.github.io/react-native/) environment
* Android environment
### Installation
```
cd ApkExtractorRN/
yarn
cd android/
./gradlew buildRelease
cd ..
adb install dist/vx.x.x/android/x.apk
```
Enjoy yourself :)
