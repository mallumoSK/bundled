package tk.mallumo.sample

import android.os.Parcel
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import tk.mallumo.bundled.Bundled

class MainActivity : AppCompatActivity()

@Bundled
open class SampleChild private constructor(var createNew: Boolean = false) : SampleParent()


class TestParcelable() : Parcelable {

    constructor(parcel: Parcel) : this() {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(p0: Parcel?, p1: Int) {

    }

    companion object CREATOR : Parcelable.Creator<TestParcelable> {
        override fun createFromParcel(parcel: Parcel): TestParcelable {
            return TestParcelable(parcel)
        }

        override fun newArray(size: Int): Array<TestParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

data class ExternalDataClass(var x: Int = 10)

@Bundled
open class SampleParent(
        internal var q: Boolean = false,
        var w: Byte = 0x0.toByte()
) {

    private val privateVal = "weearg"

    companion object {
        val publicValCompanion = 1
        var publicVarCompanion = 1
        private val privateValCompanion = 1
        private var privateVarCompanion = 1
    }

    var externalDataClass = ExternalDataClass()

    var e = 'a'
    var r = 0.0
    var t = 0.1F
    var y = 12
    var u = 50L
    var i = 10.toShort()
    var o = "wef"
    var p = ".wef".subSequence(0, 2)
    var a = booleanArrayOf(false)
    var s = byteArrayOf()
    var d = charArrayOf()
    var f = doubleArrayOf()
    var g = floatArrayOf()
    var h = intArrayOf()
    var ll = longArrayOf()
    var j = shortArrayOf()
    var k = Size(1, 2)
    var l = SizeF(1.0F, 2.4F)

    var m = bundleOf()
    var n = TestParcelable()
    var fh = arrayOf(1, 2, 2)
}