package ru.example.gnt.rickandmorty.navigation

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.presentation.detials.CharacterDetailsFragment
import ru.example.gnt.characters.presentation.list.CharacterListFragment
import ru.example.gnt.common.base.interfaces.DetailsFragment
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.episodes.EpisodesRouter
import ru.example.gnt.episodes.presentation.episode_details.EpisodeDetailsFragment
import ru.example.gnt.episodes.presentation.episode_list.EpisodeListFragment
import ru.example.gnt.locations.LocationsRouter
import ru.example.gnt.locations.presentation.list.LocationListFragment
import ru.example.gnt.rickandmorty.MainActivity
import javax.inject.Inject


@ScreenScope
class MainRouter @Inject constructor(
    private val fragmentManager: FragmentManager,
    @IdRes private val mainContainerId: Int,
    private val context: Context
) : CharactersRouter, EpisodesRouter, LocationsRouter, FragmentManager.OnBackStackChangedListener {

    init {
        fragmentManager.addOnBackStackChangedListener(this)
    }

    fun openCharactersScreen() {
        navigate(
            fragment = CharacterListFragment.createInstance(),
            tag = CharacterListFragment.CHARACTERS_FRAGMENT_TAG,
            addToBackStack = true,
        )
    }

    fun clearBackStack() {
        fragmentManager.popBackStack()
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


    override fun navigateBackToCharacters() {
        fragmentManager.popBackStack(
            CharacterListFragment.CHARACTERS_FRAGMENT_TAG,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
    fun getActiveFragment(): Fragment? {
        val f = fragmentManager.fragments.lastOrNull()
        return if (f?.isVisible == true) {
            f
        } else {
            fragmentManager.fragments.firstOrNull {
                it.isVisible
            }
        }
    }

    private fun navigate(
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean = true,
    ) {
        checkFragment(fragment)
        if (fragment is RootFragment && fragmentManager.findFragmentByTag(tag) != null) {
            val index = getIndex(tag)
            fragmentManager.popBackStack(tag, 0)

        } else {
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
            transaction.commit()
        }
    }


    override fun onBackStackChanged() {
        getActiveFragment()?.let { checkFragment(it) }
    }

    private fun checkFragment(fragment: Fragment) {
        (context as MainActivity?)?.apply {
            when (fragment) {
                is DetailsFragment -> {
                    setItemsVisibility(isVisible = false)
                    setToolbarBackButtonVisibility(isVisible = true)
                }
                is SearchFragment -> {
                    setToolbarBackButtonVisibility(isVisible = false)
                    setItemsVisibility(isVisible = true)
                }
                else -> {

                }
            }
        }
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

    override fun navigateToLocationDetails(id: Int?) {

    }

    override fun navigateToLocationList() {
        navigate(
            fragment = LocationListFragment.createInstance(),
            tag = LocationListFragment.LOCATION_LIST_FRAGMENT_TAG,
            addToBackStack = true
        )
    }
}
