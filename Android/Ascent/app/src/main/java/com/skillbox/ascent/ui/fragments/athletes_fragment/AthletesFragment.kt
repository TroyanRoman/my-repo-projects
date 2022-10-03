package com.skillbox.ascent.ui.fragments.athletes_fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.skillbox.ascent.R
import com.skillbox.ascent.databinding.FragmentAthleteBinding
import com.skillbox.ascent.utils.translit
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder

@AndroidEntryPoint
class AthletesFragment : Fragment(R.layout.fragment_athlete) {

    private val binding by viewBinding(FragmentAthleteBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val intentAction = requireActivity().intent.action
        val intentDataString = requireActivity().intent?.dataString


        if (intentAction == Intent.ACTION_VIEW &&
            intentDataString?.startsWith("https://www.strava.com/athletes") == true
        ) {

            val firstName =
                getStringDataFromIntent(
                    afterDelimiter = FIRST_NAME_DELIMITER,
                    beforeDelimiter = LAST_NAME_DELIMITER
                )
            val lastName = getStringDataFromIntent(
                afterDelimiter = LAST_NAME_DELIMITER,
                beforeDelimiter = WEIGHT_DELIMITER
            )
            val athletesWeight = getStringDataFromIntent(WEIGHT_DELIMITER, FOLLOWERS_DELIMITER)


            with(binding) {

                userName.text = resources.getString(R.string.athletes_name, firstName, lastName)

                userMail.text = setUserNameText(firstName, lastName)

                followersQty.text =
                    getStringDataFromIntent(FOLLOWERS_DELIMITER, FOLLOWINGS_DELIMITER) ?: "No data"
                followingQty.text =
                    getStringDataFromIntent(FOLLOWINGS_DELIMITER, GENDER_DELIMITER) ?: "No data"
                country.text =
                    getStringDataFromIntent(COUNTRY_NAME_DELIMITER, AVATAR_LINK_DELIMITER)
                        ?: "Not specified"
                gender.text =
                    getStringDataFromIntent(GENDER_DELIMITER, COUNTRY_NAME_DELIMITER)?.uppercase()
                        ?: "No data"
                weight.hint = resources.getString(R.string.athletes_weight, athletesWeight)


                Glide.with(avatarImage)
                    .load(
                        getStringDataFromIntent(
                            afterDelimiter = AVATAR_LINK_DELIMITER,
                            beforeDelimiter = ""
                        )
                    )
                    .placeholder(R.drawable.ic_load_avatar)
                    .error(R.drawable.ic_no_avatar)
                    .circleCrop()
                    .into(avatarImage)
            }

        } else {
            binding.mainInfoCard.visibility = View.GONE
            binding.additionalInfoCard.visibility = View.GONE
            binding.noAthleteInfo.visibility = View.VISIBLE
        }
    }

    private fun setUserNameText(userFirstName: String?, userLastName: String?): String {
        return "@" + userFirstName?.translit() + userLastName?.translit()
    }

    private fun getStringDataFromIntent(afterDelimiter: String, beforeDelimiter: String): String? {
        val encodedString = requireActivity().intent?.dataString
        val decodedString = URLDecoder.decode(encodedString, "UTF-8")
        if (decodedString?.substringAfter(afterDelimiter)
                ?.substringBeforeLast(beforeDelimiter) == "null"
        ) return "Not Specified"
        return decodedString?.substringAfter(afterDelimiter)
            ?.substringBeforeLast(beforeDelimiter)
    }

    companion object {
        private const val FIRST_NAME_DELIMITER = "?f="
        private const val LAST_NAME_DELIMITER = "&l="
        private const val AVATAR_LINK_DELIMITER = "&a="
        private const val WEIGHT_DELIMITER = "&w="
        private const val FOLLOWERS_DELIMITER = "&fls="
        private const val FOLLOWINGS_DELIMITER = "&flg="
        private const val COUNTRY_NAME_DELIMITER = "&c="
        private const val GENDER_DELIMITER = "&g="
    }
}