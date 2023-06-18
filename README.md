![Banner](/docs/image/banner-frogo-keyboard.png)
[![Jitpack Io](https://jitpack.io/v/amirisback/keyboard.svg?style=flat-square)](https://jitpack.io/#amirisback/keyboard)
[![Android CI](https://github.com/amirisback/keyboard/actions/workflows/android-ci.yml/badge.svg)](https://github.com/amirisback/keyboard/actions/workflows/android-ci.yml)
[![Scan with Detekt](https://github.com/amirisback/keyboard/actions/workflows/detekt-analysis.yml/badge.svg)](https://github.com/amirisback/keyboard/actions/workflows/detekt-analysis.yml)
[![Google Badge](https://img.shields.io/badge/Google%20Dev%20Library-keyboard-orange?style=flat-square)](https://devlibrary.withgoogle.com/products/android/repos/amirisback-keyboard)

- Simple research keyboard for Android
- Custom Keyboard
- Emoji Custom Keyboard
- Call API inside Keyboard
- Open Form inside Keyboard
- Support Dark Theme
- AutoText Feature
- Setup Toggle Feature

<a href="https://play.google.com/store/apps/details?id=com.frogobox.frogokeyboard">
  <img width="200px" height="75px" src="https://amirisback.github.io/amirisback/docs/image/google-play-badge.png">
</a>

## Version Release
This Is Latest Release

    $version_release = 1.1.4

What's New??

    * Avaiable in dark mode *
    * Enhance Performance *
    * Update Build Gradle Style Latest Version *
    * Update Emoji Category *

## How To Use As Library (Coming Soon)

### Step 1. Add the JitPack repository to your build file (build.gradle : Project)

#### <Option 1> Groovy Gradle

    // Add it in your root build.gradle at the end of repositories:

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }

#### <Option 2> Kotlin DSL Gradle

```kotlin
// Add it in your root build.gradle.kts at the end of repositories:

allprojects {
    repositories {
        ...
        maven("https://jitpack.io")
    }
}
```


### Step 2. Add the dependency (build.gradle : Module)

#### <Option 1> Groovy Gradle

    dependencies {
        // library frogo-keyboard
        implementation 'com.github.amirisback:keyboard:1.1.4'
    }

#### <Option 2> Kotlin DSL Gradle

    dependencies {
        // library frogo-keyboard
        implementation("com.github.amirisback:keyboard:1.1.4")
    }

### Step 3. Create Service Keyboard IME

#### Create Class Keyboard IME
```kotlin
class KeyboardIME : BaseKeyboardIME<YourIMELayoutBinding>() {
    
    // ovveride function from IKeyboardIME
    
}
```

#### Interface IKeyboardIME
```kotlin
interface IKeyboardIME {

    fun initialSetupKeyboard()

    fun setupBinding() 
    
    fun invalidateKeyboard()

    fun initCurrentInputConnection()
    
    fun hideMainKeyboard()

    fun showMainKeyboard()

    fun showOnlyKeyboard()
    
    fun hideOnlyKeyboard()
    
    fun EditText.showKeyboardExt()

    fun initBackToMainKeyboard()

    fun setupFeatureKeyboard()

    fun initView()

    fun invalidateAllKeys()

    @RequiresApi(Build.VERSION_CODES.M)
    fun runEmojiBoard()

    fun updateShiftKeyState()

    @RequiresApi(Build.VERSION_CODES.M)
    fun onKeyExt(code: Int, inputConnection: InputConnection)

    fun moveCursor(moveRight: Boolean)

    fun getImeOptionsActionId(): Int

    fun getKeyboardLayoutXML(): Int
    
}
```

### Step 4. Create Layout Keyboard IME
```xml
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/keyboard_holder"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/keyboard_bg_root">

    <LinearLayout
        android:id="@+id/container_keyboard_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        app:layout_constraintBottom_toBottomOf="parent">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/keyboard_header"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/keyboard_bg_root"
            android:minHeight="@dimen/frogo_dimen_64dp" />

        <com.frogobox.libkeyboard.ui.main.MainKeyboard
            android:id="@+id/keyboard_main"
            style="@style/KwKeyboardView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/theme_dark_background_color" />

    </LinearLayout>

    <LinearLayout
        android:id="@+id/mock_measure_height_keyboard"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:visibility="gone"
        app:layout_constraintBottom_toTopOf="@id/container_keyboard_main">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/mock_keyboard_header"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/keyboard_bg_root"
            android:minHeight="@dimen/frogo_dimen_64dp" />

        <com.frogobox.libkeyboard.ui.main.MainKeyboard
            android:id="@+id/mock_measure_height_keyboard_main"
            style="@style/KwKeyboardView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/theme_dark_background_color" />

    </LinearLayout>

    <com.frogobox.appkeyboard.ui.keyboard.autotext.AutoTextKeyboard
        android:id="@+id/keyboard_autotext"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clickable="true"
        android:focusable="true"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="@id/mock_measure_height_keyboard"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/mock_measure_height_keyboard" />

    <com.frogobox.appkeyboard.ui.keyboard.templatetext.TemplateTextKeyboard
        android:id="@+id/keyboard_template_text"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clickable="true"
        android:focusable="true"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="@id/mock_measure_height_keyboard"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/mock_measure_height_keyboard" />

    <com.frogobox.appkeyboard.ui.keyboard.news.NewsKeyboard
        android:id="@+id/keyboard_news"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clickable="true"
        android:focusable="true"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="@id/mock_measure_height_keyboard"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/mock_measure_height_keyboard" />

    <com.frogobox.appkeyboard.ui.keyboard.movie.MovieKeyboard
        android:id="@+id/keyboard_moview"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clickable="true"
        android:focusable="true"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="@id/mock_measure_height_keyboard"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/mock_measure_height_keyboard" />

    <com.frogobox.appkeyboard.ui.keyboard.webview.WebiewKeyboard
        android:id="@+id/keyboard_webview"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clickable="true"
        android:focusable="true"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="@id/mock_measure_height_keyboard"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/mock_measure_height_keyboard" />

    <com.frogobox.appkeyboard.ui.keyboard.form.FormKeyboard
        android:id="@+id/keyboard_form"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clickable="true"
        android:focusable="true"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="@id/mock_measure_height_keyboard"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/mock_measure_height_keyboard" />

    <com.frogobox.libkeyboard.ui.emoji.EmojiKeyboard
        android:id="@+id/keyboard_emoji"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:clickable="true"
        android:focusable="true"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="@id/mock_measure_height_keyboard"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/mock_measure_height_keyboard" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Step 5. Create KeyConfig
```xml
<?xml version="1.0" encoding="utf-8"?>
<input-method xmlns:android="http://schemas.android.com/apk/res/android"
    android:icon="@drawable/ic_frogobox"
    android:settingsActivity="com.frogobox.appkeyboard.ui.main.MainActivity">

    <subtype android:imeSubtypeMode="Keyboard" />

</input-method>

```

### Step 6. Create Keyboard Service In Manifest
```xml
<service
    android:name=".services.KeyboardIME"
    android:exported="true"
    android:label="@string/app_name"
    android:permission="android.permission.BIND_INPUT_METHOD">
    <meta-data
        android:name="android.view.im"
        android:resource="@xml/keys_config" />
    <intent-filter>
        <action android:name="android.view.InputMethod" />
    </intent-filter>
</service>
```


## Video Play
https://user-images.githubusercontent.com/24654871/231431022-4410933f-7199-4967-9db1-24544f5593e0.mp4

## Screen Shoot

### How To Activated

#### Activated Keyboard

<table>

<tr>
    <th>Welcome Page (Light)</th>
    <th>Activated Keyboard (Light)</th>
    <th>After Activated (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/activated-keyboard/light/ss_1.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/activated-keyboard/light/ss_2-1.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/activated-keyboard/light/ss_2-2.png"></td>
</tr>

<tr>
    <th>Welcome Page (Dark)</th>
    <th>Activated Keyboard (Dark)</th>
    <th>After Activated (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/activated-keyboard/dark/ss_1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/activated-keyboard/dark/ss_2-1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/activated-keyboard/dark/ss_2-2.jpeg"></td>
</tr>

</table>

#### Change Keyboard

<table>

<tr>
    <th>Before Change Keyboard (Light)</th>
    <th>Change Keyboard (Light)</th>
    <th>After Change Keyboard (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/activated-keyboard/light/ss_2-2.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/change-keyboard/light/ss_3-1.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/change-keyboard/light/ss_3-2.png"></td>
</tr>

<tr>
    <th>Before Change Keyboard (Dark)</th>
    <th>Change Keyboard (Dark)</th>
    <th>After Change Keyboard (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/activated-keyboard/dark/ss_2-2.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/change-keyboard/dark/ss_3-1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/change-keyboard/dark/ss_3-2.jpeg"></td>
</tr>

</table>


### Normal Keyboard State

<table>

<tr>
    <th>Alphabet Keyboard (Light)</th>
    <th>Numeric Keyboard (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/normal-keyboard-state/light/ss_4-1.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/normal-keyboard-state/light/ss_4-2.png"></td>
</tr>

<tr>
    <th>Alphabet Keyboard (Dark)</th>
    <th>Numeric Keyboard (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/normal-keyboard-state/dark/ss_4-1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/normal-keyboard-state/dark/ss_4-2.jpeg"></td>
</tr>

</table>

### Feature Keyboard

#### Using API

<table>

<tr>
    <th>News API (Light)</th>
    <th>Movie API (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/using-api/light/ss_5.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/using-api/light/ss_6.png"></td>
</tr>

<tr>
    <th>News API (Dark)</th>
    <th>Movie API (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/using-api/dark/ss_5.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/using-api/dark/ss_6.jpeg"></td>
</tr>

</table>

#### Webview
<table>

<tr>
    <th>Show Webview</th>
    <th>Input Webview</th>
</tr>

<tr>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/webview/light/ss_7.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/webview/light/ss_8.png"></td>
</tr>

</table>

#### Form
<table>

<tr>
    <th>Show Form (Light)</th>
    <th>Input Form (Light)</th>
    <th>Hide Keyboard (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/form/light/ss_9.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/form/light/ss_10.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/form/light/ss_11.png"></td>
</tr>

<tr>
    <th>Show Form (Dark)</th>
    <th>Input Form (Dark)</th>
    <th>Hide Keyboard (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/form/dark/ss_9.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/form/dark/ss_10.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/form/dark/ss_11.jpeg"></td>
</tr>

</table>

#### Emojis
<table>

<tr>
    <th>Emojis (Light)</th>
    <th>Emojis (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/emojis/light/ss_1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/emojis/light/ss_2.jpeg"></td>
</tr>

<tr>
    <th>Emojis (Dark)</th>
    <th>Emojis (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/emojis/dark/ss_1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/emojis/dark/ss_2.jpeg"></td>
</tr>

</table>

#### Auto Text
<table>

<tr>
    <th>Menu Auto Text (Light)</th>
    <th>Auto Text Inputed (Light)</th>
    <th>Auto Text Dashboard (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/light/ss_16-1.png"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/light/ss_16-2.png"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/light/ss_16-3.png"></td>
</tr>

<tr>
    <th>Empty View Auto Text (Light)</th>
    <th>Auto Text Editor (Light)</th>
    <th>Auto Text Detail (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/light/ss_16-4.png"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/light/ss_16-5.png"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/light/ss_16-6.png"></td>
</tr>

<tr>
    <th>Menu Auto Text (Dark)</th>
    <th>Auto Text Inputed (Dark)</th>
    <th>Auto Text Dashboard (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/dark/ss_16-1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/dark/ss_16-2.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/dark/ss_16-3.jpeg"></td>
</tr>

<tr>
    <th>Empty View Auto Text (Dark)</th>
    <th>Auto Text Editor (Dark)</th>
    <th>Auto Text Detail (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/dark/ss_16-4.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/dark/ss_16-5.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/auto-text/dark/ss_16-6.jpeg"></td>
</tr>

</table>

### Open To Other App
<table>

<tr>
    <th>Google Search (Light)</th>
    <th>Google Message (Light)</th>
    <th>Sign In Google (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/open-to-other-app/light/ss_12.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/open-to-other-app/light/ss_13.png"></td>
    <td><img width="200px" height="360px" src="https://amirisback.github.io/keyboard/docs/image/ss/open-to-other-app/light/ss_14.png"></td>
</tr>

<tr>
    <th>Google Search (Dark)</th>
    <th>Google Message (Dark)</th>
    <th>Sign In Google (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/open-to-other-app/dark/ss_12.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/open-to-other-app/dark/ss_13.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/open-to-other-app/dark/ss_14.jpeg"></td>
</tr>

</table>


### Toggle Feature
<table>

<tr>
    <th>All Toggle Off (Light)</th>
    <th>Keyboard No Feature (Light)</th>
    <th>Some Toggle On (Light)</th>
    <th>Keyboard With Feature (Light)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/toggle/ss_light_1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/toggle/ss_light_2.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/toggle/ss_light_3.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/toggle/ss_light_4.jpeg"></td>
</tr>

<tr>
    <th>All Toggle Off (Dark)</th>
    <th>Keyboard No Feature (Dark)</th>
    <th>Some Toggle On (Dark)</th>
    <th>Keyboard With Feature (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/toggle/ss_dark_1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/toggle/ss_dark_2.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/toggle/ss_dark_3.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/toggle/ss_dark_4.jpeg"></td>
</tr>

</table>


### Multi Language Support
<table>

<tr>
    <th>Menu Multi Language (Light)</th>
    <th>Change Language (Light)</th>
    <th>Menu Multi Language (Dark)</th>
    <th>Change Language (Dark)</th>
</tr>

<tr>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/multi-language/light/ss-1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/multi-language/light/ss-2.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/multi-language/dark/ss-1.jpeg"></td>
    <td><img width="200px" height="420px" src="https://amirisback.github.io/keyboard/docs/image/ss/multi-language/dark/ss-2.jpeg"></td>
</tr>

</table>

## Documentation
- https://github.com/SimpleMobileTools/Simple-Keyboard 
    - Clone From This
- https://github.com/anssih/finqwerty 
    - Keymap app for phones with physical keyboards
- https://github.com/shiftrot/caps2ctrl 
    - Provides almost all keymaps we need usually
- https://github.com/kolegad/custom-keyboard
- https://android.googlesource.com/platform/frameworks/base/+/master/data/keyboards/Generic.kl
- https://android.googlesource.com/platform/frameworks/base/+/master/data/keyboards/Generic.kcm
- https://developer.android.com/reference/kotlin/android/hardware/input/InputManager
- https://source.android.com/devices/input/key-character-map-files

## Frogo Library
<table>
    <tr>
        <th>No.</th>
        <th>Github Name / Organization</th>
        <th>Github Project</th>
    </tr>
    <tr>
        <td>1.</td>
        <td><a href="https://github.com/amirisback">Muhammad Faisal Amir</a></td>
        <td><a href="https://github.com/amirisback/frogo-admob">frogo-admob</a></td>
    </tr>
    <tr>
        <td>2.</td>
        <td><a href="https://github.com/amirisback">Muhammad Faisal Amir</a></td>
        <td><a href="https://github.com/amirisback/frogo-recycler-view">frogo-recycler-view</a></td>
    </tr>
    <tr>
        <td>3.</td>
        <td><a href="https://github.com/frogobox">Frogobox</a></td>
        <td><a href="https://github.com/frogobox/frogo-sdk">frogo-sdk</a></td>
    </tr>
    <tr>
        <td>4.</td>
        <td><a href="https://github.com/frogobox">Frogobox</a></td>
        <td><a href="https://github.com/frogobox/frogo-ui">frogo-ui</a></td>
    </tr>
    <tr>
        <td>5.</td>
        <td><a href="https://github.com/frogobox">Frogobox</a></td>
        <td><a href="https://github.com/frogobox/frogo-consume-api">frogo-consume-api</a></td>
    </tr>
</table>

## Colaborator
Very open to anyone, I'll write your name under this, please contribute by sending an email to me

- Mail To faisalamircs@gmail.com
- Subject : Github _ [Github-Username-Account] _ [Language] _ [Repository-Name]
- Example : Github_amirisback_kotlin_admob-helper-implementation

Name Of Contribute
- Muhammad Faisal Amir
- Waiting List
- Waiting List

Waiting for your contribute

## Attention !!!
- Please enjoy and don't forget fork and give a star
- Don't Forget Follow My Github Account

![ScreenShoot Apps](https://raw.githubusercontent.com/amirisback/frogo-recycler-view/master/docs/image/mad_score.png)
