package org.example.proyecto_plantilla.core.version

/**
 * Compara versiones semánticas. Kotlin puro.
 *
 * [compare] es TOLERANTE, y cada tolerancia tiene su test: compara numéricamente por segmentos (`1.9 < 1.10`); un
 * segmento que falta vale 0 (`1.2 == 1.2.0`); ignora el sufijo de pre-release (`-rc1`, `-rc.1`) y el de build
 * (`+build.7`); recorta espacios; un segmento parcialmente numérico vale sus dígitos iniciales (`3rc` → 3). Los
 * segmentos se comparan como cadenas de dígitos (sin ceros a la izquierda, por longitud y luego por texto): un segmento
 * de cualquier longitud se ordena bien, sin pasar por `Int` (#PP14).
 *
 * [isValid] es ESTRICTO, y va aparte: exactamente tres segmentos numéricos `x.y.z`, con sufijos `-…` y `+…` admitidos y
 * espacios alrededor tolerados. Lo que `compare` tolera no hace válido un valor.
 */
object SemanticVersionComparator {
    private val strict = Regex("""^\d+\.\d+\.\d+(-[0-9A-Za-z.-]+)?(\+[0-9A-Za-z.-]+)?$""")

    fun isValid(version: String): Boolean = strict.matches(version.trim())

    fun compare(
        a: String,
        b: String,
    ): Int {
        val x = segments(a)
        val y = segments(b)
        for (i in 0 until maxOf(x.size, y.size)) {
            val d = compareDigits(x.getOrElse(i) { "" }, y.getOrElse(i) { "" })
            if (d != 0) return d
        }
        return 0
    }

    /** Dígitos sin ceros a la izquierda («» vale 0): la más larga es mayor; a igual longitud, el texto. */
    private fun compareDigits(
        a: String,
        b: String,
    ): Int = if (a.length != b.length) a.length.compareTo(b.length) else a.compareTo(b)

    private fun segments(version: String): List<String> =
        version
            .trim()
            .substringBefore('+')
            .substringBefore('-')
            .split('.')
            .map { segment -> segment.takeWhile(Char::isDigit).trimStart('0') }
}
