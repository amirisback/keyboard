package com.frogobox.keyboard.ui.keyboard.templatetext

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */



object TemplateTextUtils {

    fun getTextApp(): List<TemplateTextModel> {
        return listOf(
            TemplateTextModel(1, "Easy to use interface", TemplateTextType.APP),
            TemplateTextModel(2, "Helpful feedback on reviews", TemplateTextType.APP),
            TemplateTextModel(3, "Accurate rating system", TemplateTextType.APP),
            TemplateTextModel(4, "Wide selection of apps", TemplateTextType.APP),
            TemplateTextModel(5, "Quick app installation", TemplateTextType.APP),
            TemplateTextModel(6, "Detailed app descriptions", TemplateTextType.APP),
            TemplateTextModel(7, "Regular updates for apps", TemplateTextType.APP),
            TemplateTextModel(8, "Variety of categories to choose from", TemplateTextType.APP),
            TemplateTextModel(9, "Secure payment options", TemplateTextType.APP),
            TemplateTextModel(10, "User-friendly search function", TemplateTextType.APP),
            TemplateTextModel(11, "Robust app recommendation system", TemplateTextType.APP),
            TemplateTextModel(12, "Personalized app suggestions", TemplateTextType.APP),
            TemplateTextModel(13, "Community-driven app reviews", TemplateTextType.APP),
            TemplateTextModel(14, "Easy navigation and sorting options", TemplateTextType.APP),
            TemplateTextModel(15, "Free and paid app options", TemplateTextType.APP),
            TemplateTextModel(16, "Safe and trustworthy app sources", TemplateTextType.APP),
            TemplateTextModel(17, "Option to save apps for later", TemplateTextType.APP),
            TemplateTextModel(18, "App comparison feature", TemplateTextType.APP),
            TemplateTextModel(19, "Access to developer information", TemplateTextType.APP),
            TemplateTextModel(20, "App preview videos", TemplateTextType.APP),
            TemplateTextModel(21, "Option to try apps before purchase", TemplateTextType.APP),
            TemplateTextModel(22, "In-app purchase options", TemplateTextType.APP),
            TemplateTextModel(23, "Timely bug fixes and improvements", TemplateTextType.APP),
            TemplateTextModel(24, "Consistent app performance", TemplateTextType.APP),
            TemplateTextModel(25, "Responsive customer service", TemplateTextType.APP),
            TemplateTextModel(26, "User feedback incorporated into app updates", TemplateTextType.APP),
            TemplateTextModel(27, "Reliable app ratings and reviews", TemplateTextType.APP),
            TemplateTextModel(28, "Accurate app information and details", TemplateTextType.APP),
            TemplateTextModel(29, "No intrusive ads or pop-ups", TemplateTextType.APP),
            TemplateTextModel(30, "Regular promotions and discounts", TemplateTextType.APP)
        ).shuffled()
    }

    fun getTextGame(): List<TemplateTextModel> {
        return listOf(
            TemplateTextModel(1, "Addictive gameplay", TemplateTextType.GAME),
            TemplateTextModel(2, "Challenging levels", TemplateTextType.GAME),
            TemplateTextModel(3, "Simple and intuitive controls", TemplateTextType.GAME),
            TemplateTextModel(4, "Smooth and responsive gameplay", TemplateTextType.GAME),
            TemplateTextModel(5, "Wide variety of music to play", TemplateTextType.GAME),
            TemplateTextModel(6, "Creative and unique game modes", TemplateTextType.GAME),
            TemplateTextModel(7, "Stunning graphics and visual effects", TemplateTextType.GAME),
            TemplateTextModel(8, "Exciting sound effects", TemplateTextType.GAME),
            TemplateTextModel(9, "Customizable backgrounds", TemplateTextType.GAME),
            TemplateTextModel(10, "Multiplayer options", TemplateTextType.GAME),
            TemplateTextModel(11, "Achievements and rewards system", TemplateTextType.GAME),
            TemplateTextModel(12, "Social media integration", TemplateTextType.GAME),
            TemplateTextModel(13, "Leaderboards for friendly competition", TemplateTextType.GAME),
            TemplateTextModel(14, "Regular updates with new content", TemplateTextType.GAME),
            TemplateTextModel(15, "Multiple difficulty levels", TemplateTextType.GAME),
            TemplateTextModel(16, "Offline play options", TemplateTextType.GAME),
            TemplateTextModel(17, "Engaging and entertaining gameplay", TemplateTextType.GAME),
            TemplateTextModel(18, "No intrusive ads or pop-ups", TemplateTextType.GAME),
            TemplateTextModel(19, "Option to remove ads with in-app purchase", TemplateTextType.GAME),
            TemplateTextModel(20, "Free to download and play", TemplateTextType.GAME),
            TemplateTextModel(21, "Option to purchase additional songs", TemplateTextType.GAME),
            TemplateTextModel(22, "Option to customize game settings", TemplateTextType.GAME),
            TemplateTextModel(23, "Great way to improve hand-eye coordination", TemplateTextType.GAME),
            TemplateTextModel(24, "Relaxing and stress-relieving game experience", TemplateTextType.GAME),
            TemplateTextModel(25, "Suitable for all ages", TemplateTextType.GAME),
            TemplateTextModel(26, "Highly addictive and challenging gameplay", TemplateTextType.GAME),
            TemplateTextModel(27, "Consistent and smooth gameplay performance", TemplateTextType.GAME),
            TemplateTextModel(28, "Positive reviews and ratings from users", TemplateTextType.GAME),
            TemplateTextModel(29, "Quick and easy to learn", TemplateTextType.GAME),
            TemplateTextModel(30, "Engaging and rewarding game progression", TemplateTextType.GAME)
        ).shuffled()
    }

    fun getTextSales() : List<TemplateTextModel> {
        return listOf(
            TemplateTextModel(1, "Hello! How may I assist you today?", TemplateTextType.SALE),
            TemplateTextModel(2, "Thank you for reaching out to us! How can I help?", TemplateTextType.SALE),
            TemplateTextModel(3,"Hi there! How may I be of service to you?",TemplateTextType.SALE),
            TemplateTextModel(4,"Welcome to our customer service! What can I do for you?",TemplateTextType.SALE),
            TemplateTextModel(5,"Hi, how can I help you with your inquiry today?", TemplateTextType.SALE),
            TemplateTextModel(6,"Thanks for contacting us! How can I assist you?", TemplateTextType.SALE),
            TemplateTextModel(7,"Hello! What brings you to our customer service?", TemplateTextType.SALE),
            TemplateTextModel(8,"Hi! How may I be of assistance to you today?", TemplateTextType.SALE),
            TemplateTextModel(9,"Thanks for reaching out! What do you need help with?", TemplateTextType.SALE),
            TemplateTextModel(10,"Hello, what can I help you with today?", TemplateTextType.SALE),
            TemplateTextModel(11,"Hi there! What can I assist you with?", TemplateTextType.SALE),
            TemplateTextModel(12,"Welcome to our customer service! How can I assist you?", TemplateTextType.SALE),
            TemplateTextModel(13,"Thanks for contacting us! How can I help you today?", TemplateTextType.SALE),
            TemplateTextModel(14,"Hello, what can I help you with on this fine day?", TemplateTextType.SALE),
            TemplateTextModel(15,"Hi! How may I assist you with your inquiry?", TemplateTextType.SALE),
            TemplateTextModel(16,"Thank you for reaching out to us! How can I be of assistance?", TemplateTextType.SALE),
            TemplateTextModel(17,"Hello, what can I help you with today?", TemplateTextType.SALE),
            TemplateTextModel(18,"Hi there! How can I assist you with your request?", TemplateTextType.SALE),
            TemplateTextModel(19,"Thanks for contacting us! What can I help you with today?", TemplateTextType.SALE),
            TemplateTextModel(20,"Hello! How may I assist you with your inquiry?", TemplateTextType.SALE),
            TemplateTextModel(21,"Hi! What brings you to our customer service today?", TemplateTextType.SALE),
            TemplateTextModel(22,"Thanks for contacting us! How can I assist you with your issue?", TemplateTextType.SALE),
            TemplateTextModel(23,"Welcome to our customer service! How can I be of assistance to you?", TemplateTextType.SALE),
            TemplateTextModel(24,"Hi there! How can I assist you with your request today?", TemplateTextType.SALE),
            TemplateTextModel(25,"Hello, how may I help you today?", TemplateTextType.SALE),
            TemplateTextModel(26,"Thanks for reaching out to us! How can I assist you with your concern?", TemplateTextType.SALE),
            TemplateTextModel(27,"Hi! What can I do for you on this lovely day?", TemplateTextType.SALE),
            TemplateTextModel(28,"Hello! What can I help you with today?", TemplateTextType.SALE),
            TemplateTextModel(29,"Thanks for contacting us! How can I assist you with your inquiry?", TemplateTextType.SALE),
            TemplateTextModel(30,"Hi there! How can I assist you with your request?", TemplateTextType.SALE)
        ).shuffled()
    }

}