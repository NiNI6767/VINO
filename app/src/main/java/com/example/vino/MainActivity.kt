package com.example.vino // ПЕРЕВІР, ЩОБ ЦЕЙ РЯДОК ЗБІГАВСЯ З НАЗВОЮ ТВОГО ПРОЄКТУ

import android.app.AlertDialog
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    // Змінні для вкладок та тем
    private val tabsList = mutableListOf("https://www.google.com")
    private var currentTabIndex = 0
    private var currentThemeScript: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ініціалізація всіх елементів інтерфейсу
        val webView: WebView = findViewById(R.id.myWebView)
        val urlInput: EditText = findViewById(R.id.urlInput)
        val goButton: Button = findViewById(R.id.goButton)
        val vinoButton: Button = findViewById(R.id.vinoButton)
        val themeButton: Button = findViewById(R.id.themeButton)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val newTabButton: Button = findViewById(R.id.newTabButton)

        // Налаштування WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // 1. Оновлюємо URL у списку вкладок
                url?.let { tabsList[currentTabIndex] = it }

                // 2. Оновлюємо назву вкладки на заголовок сайту
                tabLayout.getTabAt(currentTabIndex)?.text = view?.title ?: "Сайт"

                // 3. Накладаємо тему, якщо вона вибрана
                if (currentThemeScript.isNotEmpty()) {
                    view?.evaluateJavascript("javascript:(function() { $currentThemeScript })()", null)
                }
            }
        }

        // --- ЛОГІКА ВКЛАДОК ---

        // Додаємо першу вкладку при старті
        tabLayout.addTab(tabLayout.newTab().setText("Вкладка 1"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    currentTabIndex = it.position
                    webView.loadUrl(tabsList[currentTabIndex])
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        newTabButton.setOnClickListener {
            tabsList.add("https://www.google.com")
            val newTab = tabLayout.newTab().setText("Нова вкладка")
            tabLayout.addTab(newTab)
            newTab.select() // Перемикаємося на нову вкладку
        }

        // --- ЛОГІКА ТЕМ ---

        themeButton.setOnClickListener {
            val options = arrayOf("Готові теми", "Власна тема (посилання)")
            AlertDialog.Builder(this)
                .setTitle("Налаштування вигляду")
                .setItems(options) { _, which ->
                    if (which == 0) showPresetThemes(webView) else showCustomUrlDialog(webView)
                }.show()
        }

        // --- КНОПКИ КЕРУВАННЯ ---

        vinoButton.setOnClickListener {
            webView.loadUrl("https://www.google.com")
        }

        goButton.setOnClickListener {
            val query = urlInput.text.toString()
            val url = if (query.startsWith("http")) query else "https://www.google.com/search?q=$query"
            webView.loadUrl(url)
        }

        // Завантаження початкової сторінки
        webView.loadUrl(tabsList[0])
    }

    private fun showPresetThemes(webView: WebView) {
        val themes = arrayOf("Стандартна", "Темна", "Матриця")
        AlertDialog.Builder(this)
            .setTitle("Виберіть стиль")
            .setItems(themes) { _, which ->
                currentThemeScript = when (which) {
                    1 -> "document.body.style.backgroundColor = '#121212'; document.body.style.color = 'white';"
                    2 -> "document.body.style.backgroundColor = 'black'; document.body.style.color = '#00FF41';"
                    else -> "location.reload(); \"\""
                }
                webView.evaluateJavascript("javascript:(function() { $currentThemeScript })()", null)
            }.show()
    }

    private fun showCustomUrlDialog(webView: WebView) {
        val input = EditText(this)
        input.hint = "Вставте посилання на картинку"
        input.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
        input.setTextColor(android.graphics.Color.WHITE)

        // Створюємо контейнер
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL

        // Перетворюємо 24dp (стандартний відступ заголовка Android) у пікселі
        val density = resources.displayMetrics.density
        val paddingInDp = (24 * density).toInt()

        // Встановлюємо відступ саме для КОНТЕЙНЕРА (зліва та справа)
        container.setPadding(paddingInDp, 20, paddingInDp, 0)

        // Для самого EditText ставимо MATCH_PARENT, щоб він розтягнувся всередині відступів
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        input.layoutParams = lp

        container.addView(input)

        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Ваша тема")
            .setView(container) // Тепер лінія почнеться рівно під "В" у слові "Ваша"
            .setPositiveButton("Застосувати") { _, _ ->
                val url = input.text.toString()
                // ... твій код завантаження теми
            }
            .show()
    }
}