package com.skillbox.ascent.oauth_data

import net.openid.appauth.ResponseTypeValues

object AuthConfig {

    const val AUTH_URI = "https://www.strava.com/oauth/mobile/authorize"
    const val TOKEN_URI = "https://www.strava.com/oauth/token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val SCOPE = "activity:read_all,activity:write,profile:read_all,profile:write,activity:read"
    //const val SCOPE = "read"
    const val PROMPT = "auto"
    const val CLIENT_ID ="80685"
    const val CLIENT_SECRET ="a27505475ccf5935db51938a3a093dd1cb80eca4"
    const val CALLBACK_URL = "ascent://com.mystrava.tro"

    const val ACCESS_PREF_KEY = "access_token_key"
    const val REFRESH_PREF_KEY = "refresh_token_key"
    const val ID_PREF_KEY = "id_token_key"

    const val FULL_NAME_KEY = "full_name_key"
    const val AVATAR_KEY = "avatar_key"
}