# What is Message-Text-To-Speech - Android App?
  This app get a receiving message and read it clearly by google API text to speech.
  For more feuture, if the locale set in your machine is vietnamese, the app has accent prediction for non-accented Vietnamese
  <a href="https://github.com/tienthanhdhcn/Vietnamese-Accent-Prediction">VIETNAMESE PREDICTION SOURCES HERE!</a>
# How to install Message-Text-To-Speech - Android App?
### clone project
```bash
  $ git clone https://github.com/dungvan2512/Message-Text-To-Speech
```
 - Open android studio, go to `app/src/main/java/com/example/dung/messagetospeech/config/Config.java` and replace your service api of accent prediction.
 - The Source of server API accent prediction <a href="https://github.com/dungvan2512/Accent-Prediction-API">JSP SOURCES HERE!</a>

  Build the app to APK file to install in your machine and enjoy.
### note!
  if your want to build a server api response accent prediction for non-accented Vietnamese text, go to <a href="https://github.com/dungvan2512/Accent-Prediction-API"> sources </a> and build it to use.
