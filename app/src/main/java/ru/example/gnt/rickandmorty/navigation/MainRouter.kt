package ru.example.gnt.rickandmorty.navigation

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.presentation.detials.CharacterDetailsFragment
import ru.example.gnt.characters.presentation.list.CharacterListFragment
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.common.utils.extensions.showToastShort
import ru.example.gnt.episodes.EpisodesRouter
import ru.example.gnt.episodes.presentation.episode_details.EpisodeDetailsFragment
import ru.example.gnt.episodes.presentation.episode_list.EpisodeListFragment
import ru.example.gnt.locations.LocationsRouter
import ru.example.gnt.locations.presentation.details.LocationDetailsFragment
import ru.example.gnt.locations.presentation.list.LocationListFragment
import javax.inject.Inject


@ScreenScope
class MainRouter @Inject constructor(
    private val fragmentManager: FragmentManager,
    @IdRes private val mainContainerId: Int,
    private val context: Context
) : CharactersRouter, EpisodesRouter, LocationsRouter {

    var currentActiveTag: String? = null
    private var currentActiveFragment: Fragment? = null

    fun openCharactersScreen() {
        navigate(
            fragment = CharacterListFragment.createInstance(),
            tag = CharacterListFragment.CHARACTERS_FRAGMENT_TAG,
            addToBackStack = true,
        )
    }

    fun openInitialState() {
        navigate(
            fragment = CharacterListFragment.createInstance(),
            tag = null,
            addToBackStack = false,
        )
    }

    fun clearBackStack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun openEpisodesScreen() {
        navigate(
            fragment = EpisodeListFragment.createInstance(),
            tag = EpisodeListFragment.EPISODES_FRAGMENT_TAG,
            addToBackStack = true
        )
    }

    fun openLocationScreen() {
        navigate(
            fragment = LocationListFragment.createInstance(),
            tag = LocationListFragment.LOCATION_LIST_FRAGMENT_TAG,
            addToBackStack = true
        )
    }

    override fun navigateToCharacterDetails(id: Int) {
        navigate(
            fragment = CharacterDetailsFragment.createInstance(id),
            tag = CharacterDetailsFragment.CHARACTER_DETAILS_FRAGMENT_TAG,
            addToBackStack = true,
        )
    }


    fun getActiveFragment(): Fragment? {
        val f = fragmentManager.fragments.lastOrNull()
        return if (f?.isVisible == true) {
            f
        } else {
            fragmentManager.fragments.firstOrNull { it.isVisible } ?: currentActiveFragment
        }
    }

    private fun navigate(
        fragment: Fragment,
        tag: String?,
        addToBackStack: Boolean = true,
    ) {
        this.currentActiveTag = tag
        this.currentActiveFragment = fragment
        if(fragment is RootFragment && fragmentManager.findFragmentByTag(tag) != null) {
            popBackStack(tag)
            return
        }
        val transaction = fragmentManager.beginTransaction().replace(
            mainContainerId,
            fragment,
            tag,
        )
        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }
        transaction.setCustomAnimations(
            androidx.appcompat.R.anim.abc_slide_in_bottom,
            androidx.appcompat.R.anim.abc_slide_out_bottom,
            androidx.appcompat.R.anim.abc_popup_enter,
            androidx.appcompat.R.anim.abc_popup_exit
        )
        transaction.setReorderingAllowed(true)
        transaction.commitAllowingStateLoss()

    }

    fun popBackStack(tag: String?) {
        fragmentManager.popBackStack(tag, 0)
    }

    override fun navigateToEpisodeDetails(id: Int?) {
        navigate(
            fragment = EpisodeDetailsFragment.createInstance(id),
            tag = EpisodeDetailsFragment.EPISODE_DETAILS_TAG,
            addToBackStack = true
        )
    }

    private fun getIndex(tagName: String): Int {
        val manager: FragmentManager = fragmentManager
        for (i in 0 until manager.backStackEntryCount) {
            if (manager.getBackStackEntryAt(i).name.equals(tagName, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }

    override fun navigateToLocationDetails(id: Int) {
        navigate(
            fragment = LocationDetailsFragment.createInstance(id),
            tag = LocationDetailsFragment.LOCATION_DETAILS_FRAGMENT_TAG,
            addToBackStack = true
        )
    }

    override fun navigateToLocationList() {
        navigate(
            fragment = LocationListFragment.createInstance(),
            tag = LocationListFragment.LOCATION_LIST_FRAGMENT_TAG,
            addToBackStack = true
        )
    }
}
