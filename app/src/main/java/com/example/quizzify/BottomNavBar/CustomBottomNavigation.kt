package com.example.quizzify.BottomNavBar
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.example.quizzify.R
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.card.MaterialCardView

class CustomBottomNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    // Tab indices
    companion object {
        const val TAB_PRACTICE = 0
        const val TAB_LIVE_CONTEST = 1
        const val TAB_SAVED_LISTS = 2
        const val TAB_PROFILE = 3
    }

    // Tab views
    private val tabs: List<LinearLayout>
    private val indicators: List<View>
    private val icons: List<ImageView>
    private val texts: List<TextView>

    // Current selected tab
    private var currentTab = TAB_PRACTICE

    // Listener for tab selection
    private var onTabSelectedListener: ((Int) -> Unit)? = null

    // Tab colors
    private val tabColors = listOf(
        R.color.violet_500,  // Practice
        R.color.rose_500,    // Live Contest
        R.color.amber_500,   // Saved Lists
        R.color.cyan_500     // Profile
    )

    init {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.view_custom_bottom_nav, this, true)

        // Get tab views
        tabs = listOf(
            findViewById(R.id.practiceTab),
            findViewById(R.id.liveContestTab),
            findViewById(R.id.savedListsTab),
            findViewById(R.id.profileTab)
        )

        // Get indicator views
        indicators = listOf(
            findViewById(R.id.practiceIndicator),
            findViewById(R.id.liveContestIndicator),
            findViewById(R.id.savedListsIndicator),
            findViewById(R.id.profileIndicator)
        )

        // Get icon views
        icons = listOf(
            findViewById(R.id.practiceIcon),
            findViewById(R.id.liveContestIcon),
            findViewById(R.id.savedListsIcon),
            findViewById(R.id.profileIcon)
        )

        // Get text views
        texts = listOf(
            findViewById(R.id.practiceText),
            findViewById(R.id.liveContestText),
            findViewById(R.id.savedListsText),
            findViewById(R.id.profileText)
        )

        // Set up click listeners
        tabs.forEachIndexed { index, tab ->
            tab.setOnClickListener {
                selectTab(index)
            }
        }

        // Set initial tab
        selectTab(TAB_PRACTICE)
    }

    /**
     * Select a tab
     * @param position The tab position to select
     */
    fun selectTab(position: Int) {
        if (position == currentTab) return

        // Animate out old tab
        indicators[currentTab].animate()
            .alpha(0f)
            .scaleX(0.5f)
            .scaleY(0.5f)
            .setDuration(200)
            .withEndAction {
                indicators[currentTab].visibility = View.INVISIBLE
            }
            .start()

        icons[currentTab].setColorFilter(ContextCompat.getColor(context, R.color.nav_icon_color))
        texts[currentTab].setTextColor(ContextCompat.getColor(context, R.color.nav_text_color))

        // Animate in new tab
        indicators[position].apply {
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f

            // Create indicator background with light color
            val color = ContextCompat.getColor(context, tabColors[position])
            background = createIndicatorBackground(color)

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start()
        }

        // Set active colors
        val activeColor = ContextCompat.getColor(context, tabColors[position])
        icons[position].setColorFilter(activeColor)
        texts[position].setTextColor(activeColor)

        // Update current tab
        currentTab = position

        // Notify listener
        onTabSelectedListener?.invoke(position)
    }

    /**
     * Create indicator background with light color
     */
    private fun createIndicatorBackground(color: Int): android.graphics.drawable.GradientDrawable {
        val lightColor = ColorUtils.blendARGB(color, Color.WHITE, 0.9f)

        return android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.OVAL
            setColor(lightColor)
        }
    }

    /**
     * Set a listener for tab selection events
     * @param listener The listener to call when a tab is selected
     */
    fun setOnTabSelectedListener(listener: (Int) -> Unit) {
        onTabSelectedListener = listener
    }

    /**
     * Get the currently selected tab
     * @return The index of the currently selected tab
     */
    fun getCurrentTab(): Int = currentTab
}

