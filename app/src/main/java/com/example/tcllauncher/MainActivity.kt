package com.example.tcllauncher

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("tclhome", MODE_PRIVATE) }
    private lateinit var root: LinearLayout
    private lateinit var clock: TextView
    private lateinit var content: LinearLayout
    private val navigation = mutableListOf<View>()
    private var savedFocusIndex = 0
    private var settingsButton: View? = null
    private var exitButton: View? = null
    private val backgrounds = listOf(
        "Nuit" to R.drawable.wallpaper_night,
        "Océan" to R.drawable.wallpaper_ocean,
        "Forêt" to R.drawable.wallpaper_forest,
        "Galaxie" to R.drawable.wallpaper_galaxy,
        "Élégance" to R.drawable.wallpaper_elegance,
        "Montagne" to R.drawable.wallpaper_mountain
    )
    private val apps = listOf(
        Triple("YouTube", "com.google.android.youtube.tv", "▶"),
        Triple("Netflix", "com.netflix.ninja", "N"),
        Triple("Prime Video", "com.amazon.amazonvideo.livingroom", "prime"),
        Triple("Disney+", "com.disney.disneyplus", "D+")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun bgDrawable() = backgrounds.getOrNull(prefs.getInt("bg", 0))?.second ?: backgrounds[0].second
    private fun textColor() = Color.WHITE
    private fun sizeSp() = prefs.getInt("textSize", 22).toFloat()

    private fun makeButton(label: String, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = sizeSp()
            isAllCaps = false
            setTextColor(textColor())
            background = buttonBackground(false)
            setOnFocusChangeListener { view, hasFocus ->
                view.background = buttonBackground(hasFocus)
                view.scaleX = if (hasFocus) 1.05f else 1f
                view.scaleY = if (hasFocus) 1.05f else 1f
                if (hasFocus) {
                    val idx = navigation.indexOf(view)
                    if (idx >= 0) savedFocusIndex = idx
                }
            }
            minHeight = dp(64)
            setPadding(dp(18), dp(8), dp(18), dp(8))
            setOnClickListener { action() }
            isFocusable = true
            isFocusableInTouchMode = true
        }
    }

    private fun buttonBackground(focused: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            setColor(if (focused) Color.argb(225, 40, 113, 178) else Color.argb(85, 18, 27, 46))
            cornerRadius = dp(14).toFloat()
            setStroke(dp(if (focused) 3 else 1), if (focused) Color.WHITE else Color.argb(85, 255, 255, 255))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val current = navigation.indexOf(currentFocus)
        if (current >= 0) {
            val next = when (keyCode) {
                KeyEvent.KEYCODE_DPAD_RIGHT -> when (current) {
                    in 0..2 -> current + 1
                    3 -> 0
                    4 -> 5
                    else -> 4
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> when (current) {
                    in 1..3 -> current - 1
                    0 -> 3
                    5 -> 4
                    else -> 5
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> when (current) {
                    in 0..3 -> 4
                    4 -> 5
                    else -> 0
                }
                KeyEvent.KEYCODE_DPAD_UP -> when (current) {
                    4 -> 0
                    5 -> 2
                    else -> 4
                }
                else -> -1
            }
            if (next >= 0 && next < navigation.size) {
                navigation[next].requestFocus()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showHome() {
        navigation.clear()
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(42), dp(24), dp(42), dp(28))
            background = android.graphics.drawable.BitmapDrawable(resources, android.graphics.BitmapFactory.decodeResource(resources, bgDrawable())).apply { gravity = Gravity.FILL }
        }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val title = TextView(this).apply {
            text = "TCL HOME"
            textSize = 25f
            setTextColor(textColor())
            typeface = Typeface.DEFAULT_BOLD
        }
        top.addView(title, LinearLayout.LayoutParams(0, dp(54), 1f))
        clock = TextView(this).apply {
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(textColor())
            typeface = clockTypeface()
            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }
        top.addView(clock, LinearLayout.LayoutParams(dp(110), dp(54)))
        val settings = makeButton("⚙", { showSettings() }).apply { textSize = 28f; contentDescription = "Réglages" }
        val exit = makeButton("✕", { showExit() }).apply { contentDescription = "Quitter" }
        settingsButton = settings
        exitButton = exit
        top.addView(settings, LinearLayout.LayoutParams(dp(70), dp(54)))
        top.addView(exit, LinearLayout.LayoutParams(dp(70), dp(54)))
        root.addView(top)
        val subtitle = TextView(this).apply {
            text = "Vos applications, au même endroit"
            textSize = 16f
            setTextColor(textColor())
            alpha = 0.78f
            setPadding(0, dp(12), 0, dp(18))
        }
        root.addView(subtitle)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val row1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        apps.forEach { app ->
            val b = makeButton(app.first) { launchApp(app.second, app.first) }
            try {
                val icon = packageManager.getApplicationIcon(app.second)
                icon.setBounds(0, 0, dp(44), dp(44))
                b.setCompoundDrawables(icon, null, null, null)
                b.compoundDrawablePadding = dp(12)
            } catch (_: Exception) {
                // L’application absente garde un bouton avec son nom.
            }
            val lp = LinearLayout.LayoutParams(0, dp(112), 1f)
            lp.setMargins(dp(8), dp(8), dp(8), dp(8))
            row1.addView(b, lp)
            navigation.add(b)
        }
        content.addView(row1)
        val row2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        val hdmi = makeButton("▣   Source HDMI") { openInputs() }
        row2.addView(hdmi, LinearLayout.LayoutParams(dp(270), dp(84)))
        navigation.add(hdmi)
        navigation.add(settings)
        navigation.add(exit)
        content.addView(row2)
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        navigation.getOrNull(savedFocusIndex.coerceIn(0, navigation.lastIndex))?.requestFocus()
        updateClock()
    }

    private fun clockTypeface(): Typeface = when (prefs.getInt("clockTheme", 0)) {
        1 -> Typeface.create("sans-serif-condensed", Typeface.NORMAL)
        2 -> Typeface.create("serif", Typeface.BOLD)
        3 -> Typeface.create("monospace", Typeface.NORMAL)
        else -> Typeface.create("sans-serif", Typeface.BOLD)
    }

    private fun updateClock() {
        if (!::clock.isInitialized) return
        clock.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        clock.typeface = clockTypeface()
        clock.postDelayed({ if (!isFinishing && ::clock.isInitialized) updateClock() }, 30_000)
    }

    private fun launchApp(packageName: String, label: String) {
        val launch = packageManager.getLaunchIntentForPackage(packageName)
        if (launch != null) startActivity(launch)
        else AlertDialog.Builder(this).setTitle(label)
            .setMessage("Cette application n'est pas installée ou son nom de paquet diffère sur votre TV.")
            .setPositiveButton("OK", null).show()
    }

    private fun openInputs() {
        val candidates = listOf(
            Intent("android.intent.action.ALL_APPS"),
            Intent("android.settings.TV_SETTINGS"),
            Intent(Settings.ACTION_SETTINGS)
        )
        for (intent in candidates) {
            try {
                startActivity(intent)
                return
            } catch (_: Exception) {}
        }
        Toast.makeText(this, "Ouvrez les sources HDMI avec la touche Source de la télécommande.", Toast.LENGTH_LONG).show()
    }

    private fun showSettings() {
        val options = arrayOf("Fonds d’écran",
            "Taille du texte : ${prefs.getInt("textSize",22)}",
            "Thème de l'horloge : ${clockThemeName()}")
        AlertDialog.Builder(this).setTitle("Réglages TCL Home")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> AlertDialog.Builder(this).setTitle("Choisir un fond d’écran")
                        .setItems(backgrounds.map { it.first }.toTypedArray()) { _, choice ->
                            prefs.edit().putInt("bg", choice).apply(); showHome()
                        }.show()
                    1 -> chooseTextSize()
                    2 -> AlertDialog.Builder(this).setTitle("Thème de l'horloge")
                        .setItems(arrayOf("Classique", "Condensé", "Élégant", "Numérique")) { _, choice ->
                            prefs.edit().putInt("clockTheme", choice).apply(); showHome()
                        }.show()
                }
            }.setNegativeButton("Fermer", null).show()
    }

    private fun clockThemeName() = arrayOf("Classique", "Condensé", "Élégant", "Numérique")
        .getOrElse(prefs.getInt("clockTheme",0)) { "Classique" }

    private fun chooseTextSize() {
        val seek = SeekBar(this).apply { max = 22; progress = prefs.getInt("textSize",22) - 16 }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(12), dp(24), dp(4))
            addView(TextView(this@MainActivity).apply { text = "Taille du texte"; textSize = 18f })
            addView(seek)
        }
        AlertDialog.Builder(this).setTitle("Taille du texte").setView(box)
            .setPositiveButton("Appliquer") { _, _ ->
                prefs.edit().putInt("textSize", seek.progress + 16).apply(); showHome()
            }.setNegativeButton("Annuler", null).show()
    }

    private fun showExit() {
        AlertDialog.Builder(this).setTitle("Quitter TCL Home ?")
            .setMessage("L'application va se fermer. Vous pourrez la rouvrir depuis vos applications.")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Quitter") { _, _ -> finish() }.show()
    }
}