package tk.mallumo.bundled

import org.jetbrains.kotlin.ksp.getAllSuperTypes
import org.jetbrains.kotlin.ksp.getDeclaredProperties
import org.jetbrains.kotlin.ksp.isAbstract
import org.jetbrains.kotlin.ksp.processing.CodeGenerator
import org.jetbrains.kotlin.ksp.processing.KSPLogger
import org.jetbrains.kotlin.ksp.processing.Resolver
import org.jetbrains.kotlin.ksp.processing.SymbolProcessor
import org.jetbrains.kotlin.ksp.symbol.KSClassDeclaration
import java.io.File

/**
 * SymbolProcessor instance for creating
 * * annotation ``tk.mallumo.bundled.Bundled``
 * * extension functions ''fill'' and ''asBundle'' for annotated classes
 *
 * @see generateAnnotation
 * @see buildDeclarationMap
 * @see generateFill
 * @see generateAsBundle
 */
class BundledProcessor : SymbolProcessor {

    /**
     * helper of file/class management
     */
    private lateinit var codeWriter: CodeWriter

    private lateinit var options: Map<String, String>

    companion object {

        /**
         * root package for extensions, annotation
         */
        private const val bundledPackageName = "tk.mallumo.bundled"

        /**
         * qualifiedName name of generated annotation
         */
        private const val bundledAnnotationPath = "$bundledPackageName.Bundled"

        /**
         * error info, if is gradle file modified
         */
        private const val errProjectDir =
                "Inside yours gradle.build must be defined constant: 'ksp.arg(\"out\", \"\${projectDir.absolutePath}/src/main/ksp\")'"

    }


    override fun init(
            options: Map<String, String>,
            kotlinVersion: KotlinVersion,
            codeGenerator: CodeGenerator,
            logger: KSPLogger
    ) {
        this.options = options
        this.codeWriter = CodeWriter(
                directory = File(options["out"] ?: throw RuntimeException(errProjectDir)),
                rootPackage = "tk.mallumo.bundled"
        )
    }

    override fun process(resolver: Resolver) {
        generateAnnotation()
        generateFunctionPairs(resolver)
    }

    /**
     * Create annotation, whitch will be used for annotating classes
     */
    private fun generateAnnotation() {
        codeWriter.add(
                packageName = bundledPackageName,
                fileName = "Bundled.kt"
        ) {
            append(
                    """@Target(AnnotationTarget.CLASS)
annotation class Bundled""".trimIndent()
            )
        }
    }

    /**
     * main work of processor, generating extension functions
     */
    private fun generateFunctionPairs(resolver: Resolver) {
        buildDeclarationMap(resolver).forEach { entry ->
            codeWriter.add(
                    packageName = bundledPackageName,
                    fileName = "Bundled${entry.key.simpleName.asString()}.kt",
                    imports = listOf("android.os.Bundle", "androidx.core.os.bundleOf")
            ) {
                generateFill(entry, this)
                generateAsBundle(entry, this)
            }
        }
    }

    /**
     * Class + Property extractor
     * * extract annotated classes
     * * for every class find all parents
     * * extract only usable properties for Bundle type
     * @return mapped list of properties for every annotated class
     */
    private fun buildDeclarationMap(resolver: Resolver) =
            resolver.getSymbolsWithAnnotation(bundledAnnotationPath) // symbols with annotation
                    .filterIsInstance<KSClassDeclaration>() // only usable classes
                    .associateBy({ it }, { current -> // map all properties to annotated class
                        current.getAllSuperTypes() // find parents of annotated class
                                .map { it.declaration }
                                .filterIsInstance<KSClassDeclaration>()
                                .plusElement(current)
                                .map { property ->
                                    property.getDeclaredProperties() // find all properties of class
                                            .asSequence()
                                            .filter { !it.isAbstract() }
                                            .filter { it.getter != null }
                                            .filter { it.setter != null }
                                            .filter { it.extensionReceiver == null }
                                            .map { PropertyTypeHolder.get(it) } // get only usable properties
                                            .filterNotNull()
                                }
                                .flatten()
                    })

    /**
     * generate extension function 'fill' for every annotated class
     * @param entry prepared properties which will be used for generating
     */
    private fun generateFill(
            entry: Map.Entry<KSClassDeclaration, Sequence<PropertyTypeHolder>>,
            builder: StringBuilder
    ) {
        builder.apply {
            val fullName = entry.key.qualifiedName!!.asString()
            this += ("fun $fullName.fill(bundle: Bundle): $fullName {")
            entry.value.forEach {
                val fieldName = it.propertyName
                this += when (it.qualifiedName) {
                    "kotlin.Boolean" -> "\t$fieldName = bundle.getBoolean(\"$fieldName\", $fieldName)"
                    "kotlin.Byte" -> "\t$fieldName = bundle.getByte(\"$fieldName\", $fieldName)"
                    "kotlin.Char" -> "\t$fieldName = bundle.getChar(\"$fieldName\", $fieldName)"
                    "kotlin.Double" -> "\t$fieldName = bundle.getDouble(\"$fieldName\", $fieldName)"
                    "kotlin.Float" -> "\t$fieldName = bundle.getFloat(\"$fieldName\", $fieldName)"
                    "kotlin.Int" -> "\t$fieldName = bundle.getInt(\"$fieldName\", $fieldName)"
                    "kotlin.Long" -> "\t$fieldName = bundle.getLong(\"$fieldName\", $fieldName)"
                    "kotlin.Short" -> "\t$fieldName = bundle.getShort(\"$fieldName\", $fieldName)"
                    "kotlin.String" -> "\t$fieldName = bundle.getString(\"$fieldName\", $fieldName)"
                    "kotlin.CharSequence" -> "\t$fieldName = bundle.getCharSequence(\"$fieldName\", $fieldName)"
                    "kotlin.BooleanArray" -> "\t$fieldName = bundle.getBooleanArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.ByteArray" -> "\t$fieldName = bundle.getByteArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.CharArray" -> "\t$fieldName = bundle.getCharArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.DoubleArray" -> "\t$fieldName = bundle.getDoubleArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.FloatArray" -> "\t$fieldName = bundle.getFloatArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.IntArray" -> "\t$fieldName = bundle.getIntArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.LongArray" -> "\t$fieldName = bundle.getLongArray(\"$fieldName\") ?: $fieldName"
                    "kotlin.ShortArray" -> "\t$fieldName = bundle.getShortArray(\"$fieldName\") ?: $fieldName"
                    "android.util.Size" -> "\t$fieldName = bundle.getSize(\"$fieldName\") ?: $fieldName"
                    "android.util.SizeF" -> "\t$fieldName = bundle.getSizeF(\"$fieldName\") ?: $fieldName"
                    "android.os.Bundle" -> "\t$fieldName = bundle.getBundle(\"$fieldName\") ?: $fieldName"
                    "android.os.IBinder" -> "\t$fieldName = bundle.getIBinder(\"$fieldName\") ?: $fieldName"
                    "android.os.Parcelable" -> "\t$fieldName = bundle.getParcelable(\"$fieldName\") ?: $fieldName"
                    else -> throw RuntimeException("unexpected type: $it")
                }
            }
            this += "\treturn this"
            this += "}"
        }
    }

    /**
     * generate extension function 'asBundle' for every annotated class
     * @param entry prepared properties which will be used for generating
     */
    private fun generateAsBundle(
            entry: Map.Entry<KSClassDeclaration, Sequence<PropertyTypeHolder>>,
            builder: StringBuilder
    ) {
        builder.apply {
            this += ("\nfun ${entry.key.qualifiedName!!.asString()}.asBundle() = bundleOf(")
            entry.value.forEach {
                it.propertyName.let { fieldName ->
                    this += "\t\"$fieldName\" to $fieldName,"
                }
            }
            this += ")"
        }
    }

    /**
     * write generated functions + annotation into files
     */
    override fun finish() {
        codeWriter.write(codeWriter.filesCount > 1)
    }
}

private operator fun StringBuilder.plusAssign(s: String) {
    appendLine(s)
}


