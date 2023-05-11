package ru.example.gnt.rickandmorty

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.example.gnt.characters.di.provider.CharactersDepsStore
import ru.example.gnt.characters.presentation.list.CharacterListFragment
import ru.example.gnt.common.base.interfaces.LayoutBackDropManager
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.base.interfaces.ToggleActivity
import ru.example.gnt.common.base.search.SearchActivity
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.setImageDrawable
import ru.example.gnt.common.utils.extensions.showToastShort
import ru.example.gnt.episodes.di.deps.EpisodesDepsStore
import ru.example.gnt.episodes.presentation.episode_list.EpisodeListFragment
import ru.example.gnt.locations.di.LocationDependencyStore
import ru.example.gnt.locations.presentation.list.LocationListFragment
import ru.example.gnt.rickandmorty.databinding.ActivityMainBinding
import ru.example.gnt.rickandmorty.di.activity.ActivityComponent
import ru.example.gnt.rickandmorty.di.activity.DaggerActivityComponent
import ru.example.gnt.rickandmorty.navigation.MainRouter
import javax.inject.Inject


class MainActivity : AppCompatActivity(), SearchActivity, OnBackStackChangedListener,
    ToggleActivity {

    private lateinit var activityComponent: ActivityComponent
    private lateinit var binding: ActivityMainBinding

    private var searchView: SearchView? = null

    @Inject
    lateinit var mainRouter: MainRouter

    private var searchFragment: SearchFragment? = null
    private var toggleFragment: LayoutBackDropManager? = null
    private var searchCloseButton: ImageView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initDagger()
        initActionBar()
        setContentView(binding.root)
        initBottomNav()
        supportFragmentManager.addOnBackStackChangedListener(this)
        mainRouter.openInitialState()
        registerFragments(mainRouter.getActiveFragment())
    }

    private fun initBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            setFragmentExpanded()
            when (item.itemId) {
                ru.example.gnt.ui.R.id.characters -> {
                    mainRouter.openCharactersScreen()
                    mainRouter.currentActiveTag = CharacterListFragment.CHARACTERS_FRAGMENT_TAG
                }
                ru.example.gnt.ui.R.id.episodes -> {
                    mainRouter.openEpisodesScreen()
                    mainRouter.currentActiveTag = EpisodeListFragment.EPISODES_FRAGMENT_TAG
                }
                ru.example.gnt.ui.R.id.locations -> {
                    mainRouter.openLocationScreen()
                    mainRouter.currentActiveTag = LocationListFragment.LOCATION_LIST_FRAGMENT_TAG
                }
            }
            return@setOnItemSelectedListener true
        }
    }


    private fun initActionBar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.outlineProvider = null
        with(binding.btnFilter) {
            setOnClickListener {
                mainRouter.clearBackStack()
                (searchView?.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView).callOnClick()
                when (toggleFragment?.toggle()) {
                    BottomSheetBehavior.STATE_EXPANDED -> setFragmentCollapsed()
                    else -> setFragmentExpanded()
                }
            }
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun setFragmentCollapsed() {
        searchView?.isVisible = false
        binding.btnFilter.setImageDrawable(ru.example.gnt.ui.R.drawable.cross_svgrepo_com)
    }

    override fun setFragmentExpanded() {
        searchView?.isVisible = searchFragment != null
        binding.btnFilter.setImageDrawable(ru.example.gnt.ui.R.drawable.baseline_filter_list_24)
    }

    private fun setToolbarBackButtonVisibility(isVisible: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isVisible)
        supportActionBar?.setDisplayShowHomeEnabled(isVisible)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (mainRouter.getActiveFragment() !is SearchFragment) return false
        val inflater = menuInflater
        inflater.inflate(ru.example.gnt.ui.R.menu.app_bar_menu, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(ru.example.gnt.ui.R.id.search)
        searchView = searchItem?.actionView as SearchView

        searchView?.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchCloseButton =
            searchView?.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView

        searchCloseButton?.setOnClickListener {
            searchView?.setQuery(null, false)
            searchFragment?.doSearch(null)
            searchView?.onActionViewCollapsed()
        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                searchFragment?.doSearch(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })
        return true
    }


    private fun initDagger() {
        activityComponent =
            DaggerActivityComponent.builder()
                .mainContainerId(R.id.main_container)
                .fragmentManager(supportFragmentManager)
                .context(this)
                .build()
        CharactersDepsStore.charactersRouterDependency = activityComponent
        EpisodesDepsStore.routerDeps = activityComponent
        LocationDependencyStore.locationsRouterDependency = activityComponent
        activityComponent.inject(mainActivity = this)
    }


    override fun setSearchText(searchQuery: String) {
        searchView?.setQuery(searchQuery, true)
    }

    override fun closeSearchInterface() {
        searchCloseButton?.callOnClick()
    }

    private fun checkBottomNavSelectedItemId(selectedItemId: Int) {
        if (binding.bottomNav.selectedItemId != selectedItemId) {
            binding.bottomNav.selectedItemId = selectedItemId
        }
    }

    override fun onBackStackChanged() {
        hideKeyboard(binding.root)
        val fragment = mainRouter.getActiveFragment()
        unregisterFragments()
        registerFragments(fragment)
        if (fragment != null) {
            when (fragment) {
                is CharacterListFragment -> {
                    checkBottomNavSelectedItemId(
                        ru.example.gnt.ui.R.id.characters
                    )
                    setMainScreenMode()
                    mainRouter.clearBackStack()
                }
                is EpisodeListFragment -> {
                    checkBottomNavSelectedItemId(
                        ru.example.gnt.ui.R.id.episodes
                    )
                    setMainScreenMode()
                }
                is LocationListFragment -> {
                    checkBottomNavSelectedItemId(
                        ru.example.gnt.ui.R.id.locations
                    )
                    setMainScreenMode()
                }
                is SearchFragment -> {
                    registerFragments(fragment)
                }
                else -> {
                    setMainScreenItemsVisibility(isVisible = false)
                    setToolbarBackButtonVisibility(isVisible = true)
                }
            }
        }
    }



    private fun setMainScreenItemsVisibility(isVisible: Boolean) {
        searchView?.isVisible = isVisible
        binding.btnFilter.isVisible = isVisible
    }

    private fun setMainScreenMode() {
        setMainScreenItemsVisibility(isVisible = true)
        setToolbarBackButtonVisibility(isVisible = false)
    }


    private fun unregisterFragments() {
        toggleFragment = null
        searchFragment = null
    }

    private fun registerFragments(fragment: Fragment?) {
        if(fragment is SearchFragment) {
            searchFragment = fragment
        }
        if(fragment is LayoutBackDropManager) {
            toggleFragment = fragment
        }
    }

}
