package ru.example.gnt.rickandmorty.navigation

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.example.gnt.characters.CharactersRouter
import ru.example.gnt.characters.presentation.detials.CharacterDetailsFragment
import ru.example.gnt.characters.presentation.list.CharactersFragment
import ru.example.gnt.common.base.interfaces.DetailsFragment
import ru.example.gnt.common.base.interfaces.LayoutBackDropManager
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.di.scope.ScreenScope
import ru.example.gnt.episodes.presentation.episodes.EpisodesFragment
import ru.example.gnt.locations.presentation.LocationsFragment
import ru.example.gnt.rickandmorty.MainActivity
import javax.inject.Inject


@ScreenScope
class MainRouter @Inject constructor(
    private val fragmentManager: FragmentManager,
    @IdRes private val mainContainerId: Int,
    private val context: Context
) : CharactersRouter, FragmentManager.OnBackStackChangedListener {

    init {
        fragmentManager.addOnBackStackChangedListener(this)
    }

    override fun openCharactersScreen() {
        navigate(
            fragment = CharactersFragment.createInstance(),
            tag = CharactersFragment.CHARACTERS_FRAGMENT_TAG,
            addToBackStack = false,
        )
    }

    private fun clearBackStack() {
        fragmentManager.popBackStack()
    }

    fun openEpisodesScreen() {
        navigate(
            fragment = EpisodesFragment.createInstance(),
            tag = EpisodesFragment.EPISODES_FRAGMENT_TAG,
            addToBackStack = false
        )
    }

    fun openLocationScreen() {
        navigate(
            fragment = LocationsFragment.createInstance(),
            tag = LocationsFragment.LOCATIONS_FRAGMENT_TAG,
            addToBackStack = false
        )
    }

    override fun openCharacterDetails(id: Int) {

        navigate(
            fragment = CharacterDetailsFragment.createInstance(id),
            tag = CharacterDetailsFragment.CHARACTER_DETAILS_FRAGMENT_TAG,
            addToBackStack = true,
        )
    }


    override fun navigateBackToCharacters() {
        fragmentManager.popBackStack(
            CharactersFragment.CHARACTERS_FRAGMENT_TAG,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    fun toggleDropDown() : Int? {
        val fragment = getActiveFragment()
        if (fragment != null && fragment is LayoutBackDropManager) {
            return fragment.toggle()
        }
        return null
    }

    fun getActiveFragment(): Fragment? {
        val f = fragmentManager.fragments.last()
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
            fragmentManager.popBackStack(
                EpisodesFragment.EPISODES_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
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

    fun openInitialState() {
        navigateBackToCharacters()
        fragmentManager.beginTransaction().add(
            mainContainerId,
            CharactersFragment.createInstance()
        ).commitNow()
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
                is SearchFragment ->  {
                    setToolbarBackButtonVisibility(isVisible = false)
                    setItemsVisibility(isVisible = true)
                }
                else -> {
                }
            }
        }
    }
}
