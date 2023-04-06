package com.frogobox.keyboard.ui.keyboard.playstore

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */



object PlayStoreUtils {

    fun getPlayStoreTextApp(): List<PlayStoreTextModel> {
        return listOf(
            PlayStoreTextModel(1, "Easy to use interface", PlayStoreType.APP),
            PlayStoreTextModel(2, "Helpful feedback on reviews", PlayStoreType.APP),
            PlayStoreTextModel(3, "Accurate rating system", PlayStoreType.APP),
            PlayStoreTextModel(4, "Wide selection of apps", PlayStoreType.APP),
            PlayStoreTextModel(5, "Quick app installation", PlayStoreType.APP),
            PlayStoreTextModel(6, "Detailed app descriptions", PlayStoreType.APP),
            PlayStoreTextModel(7, "Regular updates for apps", PlayStoreType.APP),
            PlayStoreTextModel(8, "Variety of categories to choose from", PlayStoreType.APP),
            PlayStoreTextModel(9, "Secure payment options", PlayStoreType.APP),
            PlayStoreTextModel(10, "User-friendly search function", PlayStoreType.APP),
            PlayStoreTextModel(11, "Robust app recommendation system", PlayStoreType.APP),
            PlayStoreTextModel(12, "Personalized app suggestions", PlayStoreType.APP),
            PlayStoreTextModel(13, "Community-driven app reviews", PlayStoreType.APP),
            PlayStoreTextModel(14, "Easy navigation and sorting options", PlayStoreType.APP),
            PlayStoreTextModel(15, "Free and paid app options", PlayStoreType.APP),
            PlayStoreTextModel(16, "Safe and trustworthy app sources", PlayStoreType.APP),
            PlayStoreTextModel(17, "Option to save apps for later", PlayStoreType.APP),
            PlayStoreTextModel(18, "App comparison feature", PlayStoreType.APP),
            PlayStoreTextModel(19, "Access to developer information", PlayStoreType.APP),
            PlayStoreTextModel(20, "App preview videos", PlayStoreType.APP),
            PlayStoreTextModel(21, "Option to try apps before purchase", PlayStoreType.APP),
            PlayStoreTextModel(22, "In-app purchase options", PlayStoreType.APP),
            PlayStoreTextModel(23, "Timely bug fixes and improvements", PlayStoreType.APP),
            PlayStoreTextModel(24, "Consistent app performance", PlayStoreType.APP),
            PlayStoreTextModel(25, "Responsive customer service", PlayStoreType.APP),
            PlayStoreTextModel(26, "User feedback incorporated into app updates", PlayStoreType.APP),
            PlayStoreTextModel(27, "Reliable app ratings and reviews", PlayStoreType.APP),
            PlayStoreTextModel(28, "Accurate app information and details", PlayStoreType.APP),
            PlayStoreTextModel(29, "No intrusive ads or pop-ups", PlayStoreType.APP),
            PlayStoreTextModel(30, "Regular promotions and discounts", PlayStoreType.APP)
        )
    }

    fun getPlayStoreTextGame(): List<PlayStoreTextModel> {
        return listOf(
            PlayStoreTextModel(1, "Addictive gameplay", PlayStoreType.GAME),
            PlayStoreTextModel(2, "Challenging levels", PlayStoreType.GAME),
            PlayStoreTextModel(3, "Simple and intuitive controls", PlayStoreType.GAME),
            PlayStoreTextModel(4, "Smooth and responsive gameplay", PlayStoreType.GAME),
            PlayStoreTextModel(5, "Wide variety of music to play", PlayStoreType.GAME),
            PlayStoreTextModel(6, "Creative and unique game modes", PlayStoreType.GAME),
            PlayStoreTextModel(7, "Stunning graphics and visual effects", PlayStoreType.GAME),
            PlayStoreTextModel(8, "Exciting sound effects", PlayStoreType.GAME),
            PlayStoreTextModel(9, "Customizable backgrounds", PlayStoreType.GAME),
            PlayStoreTextModel(10, "Multiplayer options", PlayStoreType.GAME),
            PlayStoreTextModel(11, "Achievements and rewards system", PlayStoreType.GAME),
            PlayStoreTextModel(12, "Social media integration", PlayStoreType.GAME),
            PlayStoreTextModel(13, "Leaderboards for friendly competition", PlayStoreType.GAME),
            PlayStoreTextModel(14, "Regular updates with new content", PlayStoreType.GAME),
            PlayStoreTextModel(15, "Multiple difficulty levels", PlayStoreType.GAME),
            PlayStoreTextModel(16, "Offline play options", PlayStoreType.GAME),
            PlayStoreTextModel(17, "Engaging and entertaining gameplay", PlayStoreType.GAME),
            PlayStoreTextModel(18, "No intrusive ads or pop-ups", PlayStoreType.GAME),
            PlayStoreTextModel(19, "Option to remove ads with in-app purchase", PlayStoreType.GAME),
            PlayStoreTextModel(20, "Free to download and play", PlayStoreType.GAME),
            PlayStoreTextModel(21, "Option to purchase additional songs", PlayStoreType.GAME),
            PlayStoreTextModel(22, "Option to customize game settings", PlayStoreType.GAME),
            PlayStoreTextModel(23, "Great way to improve hand-eye coordination", PlayStoreType.GAME),
            PlayStoreTextModel(24, "Relaxing and stress-relieving game experience", PlayStoreType.GAME),
            PlayStoreTextModel(25, "Suitable for all ages", PlayStoreType.GAME),
            PlayStoreTextModel(26, "Highly addictive and challenging gameplay", PlayStoreType.GAME),
            PlayStoreTextModel(27, "Consistent and smooth gameplay performance", PlayStoreType.GAME),
            PlayStoreTextModel(28, "Positive reviews and ratings from users", PlayStoreType.GAME),
            PlayStoreTextModel(29, "Quick and easy to learn", PlayStoreType.GAME),
            PlayStoreTextModel(30, "Engaging and rewarding game progression", PlayStoreType.GAME)
        )
    }

}