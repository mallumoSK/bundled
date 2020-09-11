@file:Suppress("unused")

package tk.mallumo.bundled

import android.os.Bundle
import androidx.core.os.bundleOf

fun tk.mallumo.sample.SampleChild.fill(bundle: Bundle): tk.mallumo.sample.SampleChild {
    e = bundle.getChar("e", e)
    r = bundle.getDouble("r", r)
    t = bundle.getFloat("t", t)
    y = bundle.getInt("y", y)
    u = bundle.getLong("u", u)
    i = bundle.getShort("i", i)
    o = bundle.getString("o", o)
    p = bundle.getCharSequence("p", p)
    a = bundle.getBooleanArray("a") ?: a
    s = bundle.getByteArray("s") ?: s
    d = bundle.getCharArray("d") ?: d
    f = bundle.getDoubleArray("f") ?: f
    g = bundle.getFloatArray("g") ?: g
    h = bundle.getIntArray("h") ?: h
    ll = bundle.getLongArray("ll") ?: ll
    j = bundle.getShortArray("j") ?: j
    k = bundle.getSize("k") ?: k
    l = bundle.getSizeF("l") ?: l
    m = bundle.getBundle("m") ?: m
    n = bundle.getParcelable("n") ?: n
    q = bundle.getBoolean("q", q)
    w = bundle.getByte("w", w)
    createNew = bundle.getBoolean("createNew", createNew)
    return this
}

fun tk.mallumo.sample.SampleChild.asBundle() = bundleOf(
        "e" to e,
        "r" to r,
        "t" to t,
        "y" to y,
        "u" to u,
        "i" to i,
        "o" to o,
        "p" to p,
        "a" to a,
        "s" to s,
        "d" to d,
        "f" to f,
        "g" to g,
        "h" to h,
        "ll" to ll,
        "j" to j,
        "k" to k,
        "l" to l,
        "m" to m,
        "n" to n,
        "q" to q,
        "w" to w,
        "createNew" to createNew,
)
