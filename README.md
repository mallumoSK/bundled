# bundled

![https://mallumo.jfrog.io/artifactory/gradle-dev-local/tk/mallumo/bundled/](https://img.shields.io/maven-metadata/v?color=%234caf50&metadataUrl=https%3A%2F%2Fmallumo.jfrog.io%2Fartifactory%2Fgradle-dev-local%2Ftk%2Fmallumo%2Fbundled%2Fmaven-metadata.xml&style=for-the-badge "Version")


## Reason of usage
When you need send/share multiple parameters between activities, fragments, applications, ...

The simplest option is, usage of Parcelable (annotation @Parcelize),
but if something changed in sender or receiver it may cost exception to inconsistent data structure.

Another way is usage of serializable, but this is slower and may happens same problems as Parcelable

Usage of parameters individually is wasting time and a lot of boilerplate code, usage Bundle cost a lot of boilerplate code too

## Goals
This library/processor is focused on:
* remove of writing boilerplate code
* speed up compilation time as much as possible
* faster than kapt alternatives

## WARNING
This library/processor is ussing [Kotlin Symbol Processor](https://github.com/android/kotlin/tree/ksp/libraries/tools/kotlin-symbol-processing-api).

This is in testing unstable stage.

Usage in production in on your risk.


## Kotlin Symbol Processor, which generate:
1. extension function of bundle creator for annotated class
2. extension function which setup instance of annotated class with bundled parameters
3. annotation class for annotating yours classes

### EXAMPLE
#### Example of class:
```kotlin
package x.y.z
import tk.mallumo.bundled.Bundled

@Bundled
data class ExampleClass(
    var param1:String = "",
    val param2:Int = 0,
    private var param3:Int = 0)
    :ParentClass()

open class ParentClass(var itemX:Int = 0)
```
#### Generated Extension functions:
```kotlin
package tk.mallumo.bundled
import android.os.Bundle
import androidx.core.os.bundleOf

fun x.y.z.ExampleClass.fill(bundle: Bundle): x.y.z.ExampleClass {
	itemX = bundle.getInt("itemX", itemX)
	param1 = bundle.getString("param1", param1)
	return this
}

fun x.y.z.ExampleClass.asBundle() = bundleOf(
	"itemX" to itemX,
	"param1" to param1,
)
```

### Rules of processor
#### Enabled class types for properties
```
kotlin.Boolean
kotlin.Byte
kotlin.Char
kotlin.Double
kotlin.Float
kotlin.Int
kotlin.Long
kotlin.Short
kotlin.String
kotlin.CharSequence
kotlin.BooleanArray
kotlin.ByteArray
kotlin.CharArray
kotlin.DoubleArray
kotlin.FloatArray
kotlin.IntArray
kotlin.LongArray
kotlin.ShortArray
android.util.Size
android.util.SizeF
android.os.Bundle
android.os.Parcelable
```

#### Enabled parent type classes
```
android.os.IBinder
android.os.Parcelable
```

#### Enabled property motificators
```
var
public
internal
```

#### Ignored property motificators
```
val
private
abstract
```
### Everything inside ``companion object`` is ignored

## Tutorial, how to use:
You can clone this project, contains nodule **app** is setups for testing

### Directly in your project:
**1.**

On top of file **settings.gradle** add this:

```groovy
pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if ("kotlin-ksp".equals(requested.id.id)) {
                useModule("org.jetbrains.kotlin:kotlin-ksp:${requested.version}")
            }
            if ("org.jetbrains.kotlin.kotlin-ksp".equals(requested.id.id)) {
                useModule("org.jetbrains.kotlin:kotlin-ksp:${requested.version}")
            }
            if ("org.jetbrains.kotlin.ksp".equals(requested.id.id)) {
                useModule("org.jetbrains.kotlin:kotlin-ksp:${requested.version}")
            }
        }
    }
    repositories {
        gradlePluginPortal()
        maven {
            url = "https://dl.bintray.com/kotlin/kotlin-eap"
        }
        google()
    }
}
````

**2.**

Add KSP plugin + dependencies into **gradle.build** of yours app module:

```groovy
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-ksp' version "1.4.0-rc-dev-experimental-20200828"
}

apply from: 'https://raw.githubusercontent.com/mallumoSK/bundled/master/ksp.gradle'

dependencies {
    ksp "tk.mallumo:bundled:$bundled_version"
}
```

**3.**

run task:

gradlew :root-project:module:generateKSP

or in gradle tasks window run under ksp dir task generateKSP

this will generate :
* directory src/main/ksp/...
* annotation class which you can now use in code ``tk.mallumo.bundled.Bundled``

**4.**

Put annotation on any of yours 'data' classes

**5.**

run again:

gradlew :root-project:module:generateKSP

**6.**

JOB DONE :)

#### Warning
When you modify data classes run gradle task generateKSP, because KSP is not invoke every time.

#### Handling errors
Sometimes after changing parameter names during compilation may popup gradle compilation error.

**DO NOT WORRY**

just run gradle task generateKSP which fix it,

in next compilation will be everything OK

**Reason of this is:**

Generated extension functions are NOT inside 'generated' directory BUT part of source code directory.

I do not find any other option how to do in android projects.

In standard kotlin/java projects is logic of sourceDirs a little different.