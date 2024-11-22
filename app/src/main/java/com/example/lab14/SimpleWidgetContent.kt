package com.example.lab14

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.layout.*

class SimpleWidgetContent : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean("is_dark_theme", false)

        provideContent {
            GlanceTheme {
                NavigationWidget(context, isDarkTheme)
            }
        }
    }

    @Composable
    private fun NavigationWidget(context: Context, isDarkTheme: Boolean) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(if (isDarkTheme) Color.DarkGray else Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tema actual: ${if (isDarkTheme) "Oscuro" else "Claro"}",
                modifier = GlanceModifier.padding(bottom = 16.dp)
            )

            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    text = "Ir a Home",
                    onClick = actionStartActivity(createIntent(context, "home"))
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Button(
                    text = "Ir a Detalle",
                    onClick = actionStartActivity(createIntent(context, "detail"))
                )
            }

            Spacer(modifier = GlanceModifier.height(16.dp))

            Button(
                text = "Cambiar tema",
                onClick = actionUpdateTheme(context)
            )
        }
    }

    private fun createIntent(context: Context, destination: String): Intent {
        return Intent(context, MainActivity::class.java).apply {
            putExtra("destination", destination)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    private fun actionUpdateTheme(context: Context): Action {
        return actionRunCallback<UpdateThemeAction>()
    }
}

class UpdateThemeAction : ActionCallback {
    suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean("is_dark_theme", false)
        prefs.edit().putBoolean("is_dark_theme", !isDarkTheme).apply()

        // Actualizar el widget
        SimpleWidgetContent().update(context, glanceId)
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        TODO("Not yet implemented")
    }
}
