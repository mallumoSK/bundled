package x.y.z

import tk.mallumo.bundled.Bundled

@Bundled
data class ExampleClass(
        var param1: String = "",
        val param2: Int = 0,
        private var param3: Int = 0)
    : ParentClassClass()

open class ParentClassClass(var itemX: Int = 0)