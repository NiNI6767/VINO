package com.example.vino // ПЕРЕВІР, ЩОБ ЦЕЙ РЯДОК ЗБІГАВСЯ З НАЗВОЮ ТВОГО ПРОЄКТУ

import android.app.AlertDialog
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

// ... твої імпорти (додай Button якщо підкреслює червоним)

class MainActivity : AppCompatActivity() {

    private var currentThemeScript: String = "" // Тут зберігаємо активний JS код теми

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.myWebView)
        val urlInput: EditText = findViewById(R.id.urlInput)
        val goButton: Button = findViewById(R.id.goButton)
        val vinoButton: Button = findViewById(R.id.vinoButton)
        val themeButton: Button = findViewById(R.id.themeButton)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Кожного разу, коли сторінка завантажилась, накладаємо вибрану тему
                if (currentThemeScript.isNotEmpty()) {
                    view?.evaluateJavascript("javascript:(function() { $currentThemeScript })()", null)
                }
            }
        }

        webView.loadUrl("https://www.google.com")

        // КНОПКА ТЕМА
        themeButton.setOnClickListener {
            val mainOptions = arrayOf("Готові теми", "Власна тема (посилання)")
            AlertDialog.Builder(this)
                .setTitle("Налаштування вигляду")
                .setItems(mainOptions) { _, which ->
                    when (which) {
                        0 -> showPresetThemes(webView) // Відкрити список готових
                        1 -> showCustomUrlDialog(webView) // Відкрити вікно для посилання
                    }
                }
                .show()
        }

        // Кнопка VINO (Додому)
        vinoButton.setOnClickListener {
            webView.loadUrl("https://www.google.com")
        }

        // Кнопка GO
        goButton.setOnClickListener {
            val query = urlInput.text.toString()
            webView.loadUrl("https://www.google.com/search?q=$query")
        }
    }

    // МЕНЮ ГОТОВИХ ТЕМ
    private fun showPresetThemes(webView: WebView) {
        val themes = arrayOf("Стандартна (Біла)", "Темна ніч", "Матриця (Зелена)", "Кіберпанк (Рожева)")
        AlertDialog.Builder(this)
            .setTitle("Виберіть стиль")
            .setItems(themes) { _, which ->
                currentThemeScript = when (which) {
                    1 -> "document.body.style.backgroundColor = '#121212'; document.body.style.color = 'white';"
                    2 -> "document.body.style.backgroundColor = 'black'; document.body.style.color = '#00FF41'; var a = document.getElementsByTagName('a'); for(i=0;i<a.length;i++) a[i].style.color='#00FF41';"
                    3 -> "document.body.style.backgroundColor = '#2b213a'; document.body.style.color = '#ff00ff';"
                    else -> "location.reload(); \"\"" // Скидання
                }
                webView.evaluateJavascript("javascript:(function() { $currentThemeScript })()", null)
            }
            .show()
    }

    // ВІКНО ДЛЯ ВЛАСНОГО ПОСИЛАННЯ
    private fun showCustomUrlDialog(webView: WebView) {
        val input = EditText(this)
        input.hint = "https://example.com/image.jpg"
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(50, 20, 50, 0)
        input.layoutParams = params
        container.addView(input)

        AlertDialog.Builder(this)
            .setTitle("Ваше зображення")
            .setMessage("Вставте пряме посилання на картинку:")
            .setView(container)
            .setPositiveButton("Застосувати") { _, _ ->
                val url = input.text.toString()
                if (url.isNotEmpty()) {
                    currentThemeScript = """
                        document.body.style.backgroundImage = "url('$url')";
                        document.body.style.backgroundSize = "cover";
                        document.body.style.backgroundAttachment = "fixed";
                        document.body.style.color = "white";
                    """.trimIndent()
                    webView.evaluateJavascript("javascript:(function() { $currentThemeScript })()", null)
                }
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }
}