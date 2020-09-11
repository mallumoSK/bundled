@file:Suppress("unused")

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
