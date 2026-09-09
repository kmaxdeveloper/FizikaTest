package uz.kmax.fizikatest.presentation.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import kotlin.math.abs

/**
 * Universal MathView
 *
 * Qo'llab-quvvatlaydi:
 *
 * 1. Oddiy text
 *      "Salom dunyo"
 *
 * 2. Inline LaTeX
 *      "Javob: $x^2 + 2x + 1$"
 *
 * 3. Block LaTeX
 *      "$$\frac{a+b}{c}$$"
 *
 * 4. Raw LaTeX
 *      "\frac{1}{2}"
 *      "\sqrt{25}"
 *      "\int_0^1 x^2 dx"
 *
 * 5. Algebra
 *      "x^2 + 2x + 1 = 0"
 *      "3/4"
 *      "a + b = c"
 *
 * 6. Mixed content
 *      "Agar $x=5$ bo'lsa, $x^2=25$."
 *
 * 7. Firebase / JSON escaped LaTeX
 *      "\\frac{1}{2}"
 *
 * 8. Newline
 *      "Birinchi qator\nIkkinchi qator"
 *
 * 9. Markdown
 *      "**Muhim:** $x^2$"
 *
 * 10. STATIC FORMULA SIZE
 *
 *      Barcha formulalar default 24sp.
 *
 *      Auto-fit YO'Q.
 *      Width o'zgarganda qayta render YO'Q.
 *      Infinite render loop YO'Q.
 */
class MathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(
    context,
    attrs,
    defStyleAttr
) {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    companion object {

        /**
         * STATIC size o'rniga View'ning o'z textSize'idan foydalanamiz.
         * Bu XML'dagi AppTypography style'lariga moslashish imkonini beradi.
         */
        // private const val FORMULA_SIZE_SP = 18f

        /**
         * LaTeX command.
         *
         * \frac
         * \sqrt
         * \sin
         * \cos
         * ...
         */
        private val LATEX_COMMAND =
            Regex("""\\[a-zA-Z]+""")

        /**
         * Superscript / subscript.
         *
         * x^2
         * a_1
         * x^{2}
         */
        private val SUPERSCRIPT =
            Regex(
                """[A-Za-z0-9]\s*[\^_]\s*(?:[A-Za-z0-9]|\{)"""
            )

        /**
         * Algebraik operation.
         *
         * 2 + 3
         * x = 5
         * a / b
         */
        private val MATH_OPERATION =
            Regex(
                """[0-9A-Za-z)\]]\s*[\+\-\*\/\=><≤≥±×÷]\s*[0-9A-Za-z(\[]"""
            )

        /**
         * Unicode mathematical symbols.
         */
        private val MATH_SYMBOLS =
            setOf(
                '±',
                '≤',
                '≥',
                '≠',
                '≈',
                '∞',
                '√',
                '∑',
                '∏',
                '∫',
                '∂',
                '∆',
                '∇',
                '∈',
                '∉',
                '⊂',
                '⊃',
                '∪',
                '∩',
                '→',
                '←',
                '↔',
                '⇒',
                '⇔',
                'π',
                'θ',
                'α',
                'β',
                'γ',
                'δ',
                'λ',
                'μ',
                'σ',
                'φ',
                'ω'
            )

        /**
         * Existing $$...$$ block/inline formula.
         */
        private val REGEX_DOUBLE_DOLLAR =
            Regex("""\$\$[\s\S]*?\$\$""")

        /**
         * User input'dagi $...$.
         */
        private val REGEX_SINGLE_DOLLAR =
            Regex(
                """(?<!\$)\$(?!\$)([^$]*?)(?<!\$)\$(?!\$)"""
            )

        /**
         * Haqiqiy LaTeX buyruqlari ro'yxati.
         *
         * Faqat shu buyruqlar topilsa formula deb hisoblanadi.
         * \n, \t kabi escape characterlar HISOBLANMAYDI.
         */
        private val KNOWN_LATEX_COMMANDS = setOf(
            "frac", "sqrt", "int", "sum", "prod", "lim", "log", "ln", "sin", "cos", "tan",
            "sec", "csc", "cot", "arcsin", "arccos", "arctan", "sinh", "cosh", "tanh",
            "exp", "max", "min", "sup", "inf", "det", "deg", "ker", "dim", "hom",
            "arg", "gcd", "lcm", "mod", "div", "pm", "mp", "times", "cdot", "ast",
            "star", "circ", "bullet", "cap", "cup", "sqcap", "sqcup", "vee", "wedge",
            "oplus", "ominus", "otimes", "oslash", "odot", "bigcap", "bigcup", "bigsqcup",
            "bigvee", "bigwedge", "bigoplus", "bigotimes", "bigodot", "leq", "geq", "neq",
            "ll", "gg", "prec", "succ", "subset", "supset", "subseteq", "supseteq",
            "in", "notin", "ni", "equiv", "approx", "cong", "sim", "simeq",
            "perp", "parallel", "mid", "nmid", "propto", "infty", "partial",
            "nabla", "forall", "exists", "nexists", "emptyset", "varnothing",
            "mathbb", "mathbf", "mathit", "mathrm", "mathcal", "mathfrak",
            "left", "right", "big", "bigg", "Big", "Bigg",
            "hat", "bar", "dot", "ddot", "vec", "tilde", "breve", "check", "acute", "grave",
            "overline", "underline", "widehat", "widetilde", "overbrace", "underbrace",
            "alpha", "beta", "gamma", "delta", "epsilon", "varepsilon", "zeta", "eta",
            "theta", "vartheta", "iota", "kappa", "lambda", "mu", "nu", "xi",
            "pi", "varpi", "rho", "varrho", "sigma", "varsigma", "tau", "upsilon",
            "phi", "varphi", "chi", "psi", "omega",
            "Gamma", "Delta", "Theta", "Lambda", "Xi", "Pi", "Sigma", "Upsilon",
            "Phi", "Psi", "Omega",
            "displaystyle", "textstyle", "scriptstyle", "scriptscriptstyle",
            "text", "operatorname", "underset", "overset", "stackrel",
            "binom", "choose", "atop", "substack",
            "begin", "end", "matrix", "pmatrix", "bmatrix", "vmatrix", "Vmatrix",
            "cases", "align", "aligned", "array", "eqnarray",
            "quad", "qquad", "hspace", "vspace",
            "to", "gets", "Rightarrow", "Leftarrow", "Leftrightarrow",
            "rightarrow", "leftarrow", "leftrightarrow", "uparrow", "downarrow",
            "cdots", "ldots", "vdots", "ddots",
            "not", "neg", "lnot", "land", "lor",
            "color", "colorbox", "boxed",
            "dfrac", "tfrac", "cfrac",
            "limits", "nolimits",
            "le", "ge", "ne", "lt", "gt"
        )

        /**
         * Tabiiy tildagi "oddiy so'z" — faqat harflardan iborat, 3+ belgili.
         *
         * Bu regex topilsa, matn formula emas, oddiy text ehtimoli yuqori.
         */
        private val NATURAL_WORD =
            Regex("""[A-Za-zА-Яа-яЎўҚқҒғҲҳ']{3,}""")
    }

    // =========================================================================
    // STATE
    // =========================================================================

    /**
     * Normalize qilingan original content.
     */
    var rawText: String = ""
        private set

    /**
     * Oxirgi render qilingan content.
     */
    private var renderedContent: String = ""

    /**
     * Hozir ishlatilayotgan formula size.
     *
     * Har doim 24sp, faqat debug uchun saqlanadi.
     */
    private var currentSizePx: Float = Float.NaN

    /**
     * Markwon qaysi size bilan yaratilgan.
     */
    private var markwonSizePx: Float = Float.NaN

    /**
     * Markwon instance.
     *
     * 24sp bilan bir marta yaratiladi.
     */
    private var markwon: Markwon? = null

    /**
     * View hali attach bo'lmagan paytda content shu yerda turadi.
     */
    private var pendingContent: String? = null

    /**
     * Render recursion'dan himoya.
     */
    private var isRendering = false

    /**
     * View attach bo'lganmi?
     */
    private var isReadyForRendering = false

    // =========================================================================
    // INIT
    // =========================================================================

    init {

        isClickable = false
        isFocusable = false

        movementMethod = null

        /**
         * Formula balandligi to'g'ri chiqishi uchun.
         */
        includeFontPadding = true

        /**
         * Horizontal scrolling kerak emas.
         */
        isHorizontalScrollBarEnabled = false

        /**
         * Vertical scrolling ham kerak emas.
         */
        isVerticalScrollBarEnabled = false

        /**
         * Ellipsize ishlatmaymiz.
         */
        ellipsize = null
    }

    // =========================================================================
    // PUBLIC API
    // =========================================================================

    /**
     * Content render qiladi.
     *
     * Formula default:
     *
     * 24sp
     *
     * custom size endi yo'q.
     */
    fun setLatex(
        content: String?
    ) {

        if (content.isNullOrBlank()) {
            clearMathView()
            return
        }

        val normalized =
            normalize(content)

        rawText = normalized

        /**
         * View hali attach/o'lchanmagan bo'lsa,
         * keyin render qilamiz.
         */
        if (
            !isReadyForRendering ||
            width <= 0
        ) {

            pendingContent = normalized

            return
        }

        render(
            content = normalized,
            force = true
        )
    }

    /**
     * MathView'ni tozalaydi.
     */
    fun clearMathView() {

        rawText = ""
        renderedContent = ""
        pendingContent = null

        currentSizePx = Float.NaN
        markwonSizePx = Float.NaN

        markwon = null

        text = ""
    }

    /**
     * Normalize qilingan original content.
     */
    fun getNormalizedText(): String {
        return rawText
    }

    /**
     * Content ichida math mavjudligini tekshiradi.
     */
    fun containsMath(): Boolean {
        return containsMathContent(rawText)
    }

    /**
     * Hozirgi formula size'ni PX'da qaytaradi.
     *
     * Debug uchun.
     */
    fun getCurrentTextSizePx(): Float {
        return currentSizePx
    }

    /**
     * Hozirgi formula size'ni SP'da qaytaradi.
     */
    fun getCurrentFormulaSizeSp(): Float {
        val density = resources.displayMetrics.scaledDensity
        return if (density > 0) textSize / density else 18f
    }

    // =========================================================================
    // LIFECYCLE
    // =========================================================================

    override fun onAttachedToWindow() {

        super.onAttachedToWindow()

        isReadyForRendering = true

        val content =
            pendingContent

        if (
            content != null &&
            width > 0
        ) {

            pendingContent = null

            /**
             * View attach bo'lganidan keyin
             * bir marta render qilamiz.
             *
             * Bu infinite loop emas:
             * onAttachedToWindow -> render
             *
             * onSizeChanged render QILMAYDI.
             */
            post {

                if (
                    isReadyForRendering &&
                    width > 0 &&
                    content == rawText
                ) {

                    render(
                        content = content,
                        force = true
                    )
                }
            }
        }
    }

    override fun onDetachedFromWindow() {

        isReadyForRendering = false

        super.onDetachedFromWindow()
    }

    /**
     * MUHIM:
     *
     * Width o'zgarganda MathView qayta render qilmaydi.
     *
     * Bu intentional.
     *
     * Sabab:
     *
     * onSizeChanged()
     *      ↓
     * render()
     *      ↓
     * layout
     *      ↓
     * onSizeChanged()
     *      ↓
     * render()
     *
     * kabi infinite render loop'larning oldini olish.
     */
    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {

        super.onSizeChanged(
            w,
            h,
            oldw,
            oldh
        )
    }

    // =========================================================================
    // RENDER
    // =========================================================================

    private fun render(
        content: String,
        force: Boolean = false
    ) {

        /**
         * Recursive render himoyasi.
         */
        if (isRendering) {
            return
        }

        /**
         * View hali tayyor emas.
         */
        if (
            !isReadyForRendering &&
            width <= 0
        ) {

            pendingContent = content

            return
        }

        /**
         * Width hali mavjud emas.
         */
        if (width <= 0) {

            pendingContent = content

            return
        }

        /**
         * XML'dagi style'dan (savol: 18sp / javob: 16sp) kelgan o'lchamni PX'da olamiz.
         */
        val targetSizePx = this.textSize

        /**
         * Bir xil content + bir xil size bo'lsa
         * qayta render shart emas.
         */
        if (
            !force &&
            renderedContent == content &&
            approximatelyEqual(
                currentSizePx,
                targetSizePx
            )
        ) {

            return
        }

        isRendering = true

        try {

            val markdown =
                prepareContent(content)

            ensureMarkwon(
                sizePx = targetSizePx
            )

            try {

                markwon?.setMarkdown(
                    this,
                    markdown
                )

                renderedContent = content

                currentSizePx =
                    targetSizePx

                movementMethod = null

            } catch (_: Throwable) {

                /**
                 * LaTeX parser yiqilsa,
                 * app crash bo'lmasin.
                 */
                text = content

                renderedContent = content

                currentSizePx =
                    Float.NaN
            }

        } finally {

            isRendering = false
        }
    }

    // =========================================================================
    // FORMULA SIZE
    // =========================================================================

    /**
     * Formula size endi dinamik ravishda View'dan olinadi.
     */
    // private fun getFormulaSizePx(): Float { ... }

    // =========================================================================
    // MARKWON
    // =========================================================================

    /**
     * Markwon faqat size o'zgarganda qayta yaratiladi.
     *
     * Oddiy holatda:
     *
     * first render
     *      ↓
     * create Markwon
     *
     * keyingi renderlar:
     *
     * existing Markwon
     *      ↓
     * setMarkdown
     */
    private fun ensureMarkwon(
        sizePx: Float
    ) {

        if (
            markwon != null &&
            approximatelyEqual(
                markwonSizePx,
                sizePx
            )
        ) {

            return
        }

        markwon =
            Markwon.builder(context)

                /**
                 * Inline LaTeX parsing.
                 */
                .usePlugin(
                    MarkwonInlineParserPlugin.create()
                )

                /**
                 * JLatexMath.
                 */
                .usePlugin(
                    JLatexMathPlugin.create(
                        sizePx
                    ) { builder ->

                        /**
                         * Inline formula.
                         */
                        builder.inlinesEnabled(
                            true
                        )

                        /**
                         * Block formula.
                         */
                        builder.blocksEnabled(
                            true
                        )

                        /**
                         * Legacy block parser o'chiriladi.
                         */
                        builder.blocksLegacy(
                            false
                        )
                    }
                )

                .build()

        markwonSizePx =
            sizePx
    }

    // =========================================================================
    // CONTENT PREPARATION
    // =========================================================================

    /**
     * Contentni Markwon/JLatexMath uchun tayyorlaydi.
     */
    private fun prepareContent(
        content: String
    ): String {

        if (content.isBlank()) {
            return ""
        }

        /**
         * Explicit $ yoki $$.
         */
        if (
            containsExplicitMathDelimiter(
                content
            )
        ) {

            return injectDisplayStyleSafely(
                content
            )
        }

        /**
         * Raw LaTeX / algebra.
         */
        if (
            looksLikeStandaloneMath(
                content
            )
        ) {

            return wrapStandaloneMath(
                content
            )
        }

        /**
         * Oddiy text / Markdown.
         */
        return content
    }

    // =========================================================================
    // DOLLAR NORMALIZATION
    // =========================================================================

    /**
     * User syntax:
     *
     * $x^2$
     *
     * JLatexMath syntax:
     *
     * $$\displaystyle x^2$$
     *
     * Existing:
     *
     * $$x^2$$
     *
     * ham saqlanadi va displaystyle qo'shiladi.
     */
    private fun injectDisplayStyleSafely(
        text: String
    ): String {

        if (text.isEmpty()) {
            return text
        }

        val result =
            StringBuilder(
                text.length + 32
            )

        var index = 0

        while (index < text.length) {

            // =================================================================
            // ESCAPED DOLLAR
            // =================================================================

            if (
                text[index] == '\\' &&
                index + 1 < text.length &&
                text[index + 1] == '$'
            ) {

                result.append("\\$")

                index += 2

                continue
            }

            // =================================================================
            // CODE SPAN
            // =================================================================

            if (text[index] == '`') {

                val end =
                    findClosingBacktick(
                        text,
                        index
                    )

                if (end >= 0) {

                    result.append(
                        text,
                        index,
                        end + 1
                    )

                    index =
                        end + 1

                    continue
                }
            }

            // =================================================================
            // DOUBLE DOLLAR
            // =================================================================

            if (
                text[index] == '$' &&
                index + 1 < text.length &&
                text[index + 1] == '$'
            ) {

                val end =
                    findDoubleDollarEnd(
                        text,
                        index + 2
                    )

                if (end >= 0) {

                    val inner =
                        text.substring(
                            index + 2,
                            end
                        ).trim()

                    result.append(
                        "\$\$"
                    )

                    if (inner.isNotEmpty()) {

                        if (
                            startsWithDisplayStyle(
                                inner
                            )
                        ) {

                            result.append(
                                inner
                            )

                        } else {

                            result.append(
                                "\\displaystyle "
                            )

                            result.append(
                                inner
                            )
                        }
                    }

                    result.append(
                        "\$\$"
                    )

                    index =
                        end + 2

                    continue
                }

                /**
                 * Yopilmagan $$.
                 */
                result.append(
                    "\$\$"
                )

                index += 2

                continue
            }

            // =================================================================
            // SINGLE DOLLAR
            // =================================================================

            if (text[index] == '$') {

                val end =
                    findSingleDollarEnd(
                        text,
                        index + 1
                    )

                if (end >= 0) {

                    val inner =
                        text.substring(
                            index + 1,
                            end
                        ).trim()

                    /**
                     * Empty:
                     *
                     * $$
                     */
                    if (inner.isEmpty()) {

                        result.append(
                            "\$\$"
                        )

                        index =
                            end + 1

                        continue
                    }

                    /**
                     * Currency:
                     *
                     * $5$
                     * $25.50$
                     *
                     * math emas.
                     */
                    if (
                        isLikelyCurrencyValue(
                            inner
                        )
                    ) {

                        result.append(
                            "\$"
                        )

                        result.append(
                            inner
                        )

                        result.append(
                            "\$"
                        )

                        index =
                            end + 1

                        continue
                    }

                    /**
                     * $x^2$
                     *
                     * ->
                     *
                     * $$\displaystyle x^2$$
                     */
                    result.append(
                        "\$\$"
                    )

                    if (
                        !startsWithDisplayStyle(
                            inner
                        )
                    ) {

                        result.append(
                            "\\displaystyle "
                        )
                    }

                    result.append(
                        inner
                    )

                    result.append(
                        "\$\$"
                    )

                    index =
                        end + 1

                    continue
                }

                /**
                 * Yopilmagan $.
                 */
                result.append(
                    "\$"
                )

                index++

                continue
            }

            // =================================================================
            // NORMAL CHARACTER
            // =================================================================

            result.append(
                text[index]
            )

            index++
        }

        return result.toString()
    }

    // =========================================================================
    // RAW MATH
    // =========================================================================

    /**
     * Raw formula:
     *
     * \frac{1}{2}
     *
     * ->
     *
     * $$\displaystyle \frac{1}{2}$$
     */
    private fun wrapStandaloneMath(
        text: String
    ): String {

        val trimmed =
            text.trim()

        /**
         * Agar allaqachon $$ bilan o'ralgan bo'lsa,
         * o'z holicha qoldiramiz.
         */
        if (
            trimmed.startsWith("\$\$")
        ) {

            return trimmed
        }

        return "\$\$\\displaystyle $trimmed\$\$"
    }

    // =========================================================================
    // NORMALIZATION
    // =========================================================================

    /**
     * Firebase / JSON escaped LaTeX:
     *
     * "\\frac{1}{2}"
     *
     * ->
     *
     * "\frac{1}{2}"
     *
     * Literal:
     *
     * "\n"
     *
     * ->
     *
     * real newline.
     */
    private fun normalize(
        raw: String
    ): String {

        var result =
            raw

        /**
         * Literal newline.
         */
        result =
            result.replace(
                "\\n",
                "\n"
            )

        /**
         * Firebase / JSON escaped LaTeX.
         *
         * Faqat LaTeX command oldidagi
         * double slashni kamaytiramiz.
         *
         * Global "\\" -> "\" qilmaymiz.
         */
        result =
            result.replace(
                Regex(
                    """\\\\(?=[a-zA-Z])"""
                )
            ) {
                "\\"
            }

        /**
         * Windows newline.
         */
        result =
            result.replace(
                "\r\n",
                "\n"
            )

        /**
         * Old Mac newline.
         */
        result =
            result.replace(
                "\r",
                "\n"
            )

        return result.trim()
    }

    // =========================================================================
    // MATH DETECTION
    // =========================================================================

    private fun containsExplicitMathDelimiter(
        text: String
    ): Boolean {

        var i = 0

        while (i < text.length) {

            /**
             * Escaped dollar:
             *
             * \$
             */
            if (
                text[i] == '\\' &&
                i + 1 < text.length &&
                text[i + 1] == '$'
            ) {

                i += 2

                continue
            }

            if (text[i] == '$') {
                return true
            }

            i++
        }

        return false
    }

    private fun looksLikeStandaloneMath(
        text: String
    ): Boolean {

        val trimmed = text.trim()

        if (trimmed.isEmpty()) {
            return false
        }

        /**
         * Tabiiy til aniqlash.
         *
         * Agar matndagi "oddiy so'zlar" (3+ harf) soni ko'p bo'lsa,
         * bu formula emas, oddiy jumlalardir.
         *
         * Misol:
         *   "10-sinf uchun masalalar" → 3 ta oddiy so'z → NOT math
         *   "x^2 + 2x = 5"           → 0 ta oddiy so'z → math
         *   "F = ma yoki F=ma"        → 2 ta oddiy so'z → NOT math
         */
        val naturalWordCount = NATURAL_WORD.findAll(trimmed).count()
        if (naturalWordCount >= 2) {
            return false
        }

        /**
         * Haqiqiy LaTeX buyrug'i: \frac, \sqrt, \sin ...
         *
         * \n, \t kabi escape char EMAS.
         */
        val latexMatches = LATEX_COMMAND.findAll(trimmed)
        for (match in latexMatches) {
            val cmd = match.value.removePrefix("\\")
            if (cmd in KNOWN_LATEX_COMMANDS) {
                return true
            }
        }

        /**
         * Superscript/subscript: x^2, a_1, x^{n}
         *
         * Lekin "10-sinf" kabi oddiy so'zlarda _ yoki ^ yo'q.
         */
        if (SUPERSCRIPT.containsMatchIn(trimmed)) {
            return true
        }

        /**
         * Algebraik amal: 2+3, x=5, a/b
         *
         * Faqat 1 ta oddiy so'z yoki undan kam bo'lsa (naturalWordCount < 2)
         * bu nuqtaga yetib kelganmiz, shuning uchun qo'shimcha tekshiruv kerak emas.
         */
        if (MATH_OPERATION.containsMatchIn(trimmed)) {
            return true
        }

        /**
         * Unicode matematika belgilari.
         */
        if (trimmed.any { it in MATH_SYMBOLS }) {
            return true
        }

        /**
         * LaTeX grupplash: {a+b}.
         */
        if (trimmed.contains('{') && trimmed.contains('}')) {
            return true
        }

        return false
    }

    private fun containsMathContent(
        text: String
    ): Boolean {

        if (text.isBlank()) {
            return false
        }

        if (
            containsExplicitMathDelimiter(
                text
            )
        ) {

            return true
        }

        if (
            LATEX_COMMAND
                .containsMatchIn(text)
        ) {

            return true
        }

        if (
            SUPERSCRIPT
                .containsMatchIn(text)
        ) {

            return true
        }

        if (
            MATH_OPERATION
                .containsMatchIn(text)
        ) {

            return true
        }

        return text.any {
            it in MATH_SYMBOLS
        }
    }

    // =========================================================================
    // PARSER HELPERS
    // =========================================================================

    private fun findClosingBacktick(
        text: String,
        start: Int
    ): Int {

        var i =
            start + 1

        while (i < text.length) {

            if (text[i] == '`') {
                return i
            }

            i++
        }

        return -1
    }

    private fun findDoubleDollarEnd(
        text: String,
        start: Int
    ): Int {

        var i =
            start

        while (
            i + 1 < text.length
        ) {

            if (
                text[i] == '$' &&
                text[i + 1] == '$' &&
                !isEscaped(
                    text,
                    i
                )
            ) {

                return i
            }

            i++
        }

        return -1
    }

    private fun findSingleDollarEnd(
        text: String,
        start: Int
    ): Int {

        var i =
            start

        while (i < text.length) {

            if (
                text[i] == '$' &&
                !isEscaped(
                    text,
                    i
                )
            ) {

                val previousIsDollar =
                    i > 0 &&
                            text[i - 1] == '$'

                val nextIsDollar =
                    i + 1 < text.length &&
                            text[i + 1] == '$'

                if (
                    !previousIsDollar &&
                    !nextIsDollar
                ) {

                    return i
                }
            }

            i++
        }

        return -1
    }

    private fun isEscaped(
        text: String,
        index: Int
    ): Boolean {

        var slashCount = 0

        var i =
            index - 1

        while (
            i >= 0 &&
            text[i] == '\\'
        ) {

            slashCount++

            i--
        }

        return slashCount % 2 == 1
    }

    private fun startsWithDisplayStyle(
        text: String
    ): Boolean {

        return text
            .trimStart()
            .startsWith(
                "\\displaystyle"
            )
    }

    private fun isLikelyCurrencyValue(
        value: String
    ): Boolean {

        val normalized =
            value.trim()

        if (normalized.isEmpty()) {
            return false
        }

        return normalized.matches(
            Regex(
                """\d+(?:[.,]\d+)?"""
            )
        )
    }

    // =========================================================================
    // UTILS
    // =========================================================================

    private fun approximatelyEqual(
        a: Float,
        b: Float
    ): Boolean {

        if (
            a.isNaN() ||
            b.isNaN()
        ) {

            return false
        }

        return abs(a - b) < 0.5f
    }
}