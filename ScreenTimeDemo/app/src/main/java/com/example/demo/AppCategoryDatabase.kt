package com.example.demo

/**
 * Categories based on Google Play Store
 */
enum class AppCategory {
    ENTERTAINMENT,
    SOCIAL,
    PRODUCTIVITY,
    COMMUNICATION,
    GAME,
    TOOLS,
    FINANCE,
    EDUCATION,
    SHOPPING,
    TRAVEL,
    HEALTH_FITNESS,
    NEWS,
    MAPS_NAVIGATION,
    FOOD_DRINK,
    LIFESTYLE,
    OTHER
}

/**
 * A local database of popular app package names and their categories.
 * This provides more accurate categorization than just keyword matching.
 */
object AppCategoryDatabase {

    // Map of package name to category
    private val appCategories = mapOf(
        // Entertainment Apps
        "com.google.android.youtube" to AppCategory.ENTERTAINMENT,
        "com.netflix.mediaclient" to AppCategory.ENTERTAINMENT,
        "com.spotify.music" to AppCategory.ENTERTAINMENT,
        "com.pandora.android" to AppCategory.ENTERTAINMENT,
        "com.hulu.plus" to AppCategory.ENTERTAINMENT,
        "com.disney.disneyplus" to AppCategory.ENTERTAINMENT,
        "com.amazon.avod.thirdpartyclient" to AppCategory.ENTERTAINMENT,
        "tv.twitch.android.app" to AppCategory.ENTERTAINMENT,
        "com.google.android.youtube.music" to AppCategory.ENTERTAINMENT,
        "com.google.android.videos" to AppCategory.ENTERTAINMENT,
        "com.hbo.hbonow" to AppCategory.ENTERTAINMENT,
        "com.tivo.tivoglobal" to AppCategory.ENTERTAINMENT,
        "com.plexapp.android" to AppCategory.ENTERTAINMENT,
        "com.apple.android.music" to AppCategory.ENTERTAINMENT,
        "com.apple.android.tv" to AppCategory.ENTERTAINMENT,
        "com.vudu.android" to AppCategory.ENTERTAINMENT,
        "com.iheartradio.android" to AppCategory.ENTERTAINMENT,
        "deezer.android.app" to AppCategory.ENTERTAINMENT,
        "com.tidal.wave" to AppCategory.ENTERTAINMENT,
        "fm.last.android" to AppCategory.ENTERTAINMENT,
        "com.audible.application" to AppCategory.ENTERTAINMENT,
        "com.mxtech.videoplayer.ad" to AppCategory.ENTERTAINMENT,
        "com.royalcast" to AppCategory.ENTERTAINMENT,
        "com.royalkingdom" to AppCategory.ENTERTAINMENT,

        // Social Media Apps
        "com.facebook.katana" to AppCategory.SOCIAL,
        "com.instagram.android" to AppCategory.SOCIAL,
        "com.twitter.android" to AppCategory.SOCIAL,
        "com.snapchat.android" to AppCategory.SOCIAL,
        "com.whatsapp" to AppCategory.SOCIAL,
        "com.facebook.orca" to AppCategory.SOCIAL,
        "org.telegram.messenger" to AppCategory.SOCIAL,
        "com.tencent.mm" to AppCategory.SOCIAL, // WeChat
        "jp.naver.line.android" to AppCategory.SOCIAL,
        "com.discord" to AppCategory.SOCIAL,
        "com.reddit.frontpage" to AppCategory.SOCIAL,
        "com.linkedin.android" to AppCategory.SOCIAL,
        "com.pinterest" to AppCategory.SOCIAL,
        "com.tumblr" to AppCategory.SOCIAL,
        "kik.android" to AppCategory.SOCIAL,
        "com.skype.raider" to AppCategory.SOCIAL,
        "com.tinder" to AppCategory.SOCIAL,
        "com.bumble.app" to AppCategory.SOCIAL,
        "com.grindrapp.android" to AppCategory.SOCIAL,
        "com.okcupid.okcupid" to AppCategory.SOCIAL,
        "com.gotinder.tinder" to AppCategory.SOCIAL,
        "com.hinge.app" to AppCategory.SOCIAL,
        "com.match.android.matchmobile" to AppCategory.SOCIAL,
        "com.sina.weibo" to AppCategory.SOCIAL,
        "com.zhiliaoapp.musically" to AppCategory.SOCIAL, // TikTok
        "com.ss.android.ugc.trill" to AppCategory.SOCIAL, // TikTok
        "com.vkontakte.android" to AppCategory.SOCIAL,
        "com.rednote" to AppCategory.SOCIAL,

        // Games - these are treated as Entertainment for blocking purposes
        "com.supercell.clashofclans" to AppCategory.GAME,
        "com.king.candycrushsaga" to AppCategory.GAME,
        "com.rovio.angrybirds" to AppCategory.GAME,
        "com.mojang.minecraftpe" to AppCategory.GAME,
        "com.activision.callofduty.shooter" to AppCategory.GAME,
        "com.tencent.ig" to AppCategory.GAME, // PUBG Mobile
        "com.gameloft.android.ANMP.GloftA8HM" to AppCategory.GAME, // Asphalt 8
        "com.ea.games.simcitymobile" to AppCategory.GAME,
        "com.playrix.township" to AppCategory.GAME,
        "com.dts.freefireth" to AppCategory.GAME,
        "com.epicgames.fortnite" to AppCategory.GAME,
        "com.riotgames.legendsofruneterra" to AppCategory.GAME,
        "com.blizzard.wtcg.hearthstone" to AppCategory.GAME,
        "com.ninjakiwi.bloonstd6" to AppCategory.GAME,
        "com.nintendo.zaka" to AppCategory.GAME, // Animal Crossing
        "com.nintendo.zara" to AppCategory.GAME, // Zelda

        // Productivity
        "com.google.android.gm" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.docs" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.spreadsheets" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.docs.editors.slides" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.outlook" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.word" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.excel" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.powerpoint" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.onenote" to AppCategory.PRODUCTIVITY,
        "com.evernote" to AppCategory.PRODUCTIVITY,
        "com.todoist" to AppCategory.PRODUCTIVITY,
        "com.notion.android" to AppCategory.PRODUCTIVITY,
        "org.mozilla.firefox" to AppCategory.PRODUCTIVITY,
        "com.android.chrome" to AppCategory.PRODUCTIVITY,

        // Add more apps based on your needs...
    )

    /**
     * Get the category of an app based on its package name.
     *
     * @param packageName The package name of the app
     * @return The category of the app, or OTHER if not found
     */
    fun getCategory(packageName: String): AppCategory {
        return appCategories[packageName] ?: AppCategory.OTHER
    }

    /**
     * Check if an app belongs to a specific category.
     *
     * @param packageName The package name of the app
     * @param category The category to check
     * @return True if the app belongs to the category, false otherwise
     */
    fun isInCategory(packageName: String, category: AppCategory): Boolean {
        val appCategory = appCategories[packageName] ?: return false

        // Special case: treat GAME as ENTERTAINMENT for blocking purposes
        if (category == AppCategory.ENTERTAINMENT && appCategory == AppCategory.GAME) {
            return true
        }

        return appCategory == category
    }

    /**
     * Get all apps that belong to a specific category.
     *
     * @param category The category to filter by
     * @return A list of package names of apps in the category
     */
    fun getAppsByCategory(category: AppCategory): List<String> {
        val result = mutableListOf<String>()

        for ((packageName, appCategory) in appCategories) {
            if (appCategory == category) {
                result.add(packageName)
            }

            // Special case: include GAME in ENTERTAINMENT
            if (category == AppCategory.ENTERTAINMENT && appCategory == AppCategory.GAME) {
                result.add(packageName)
            }
        }

        return result
    }
}