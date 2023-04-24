package ru.example.gnt.rickandmorty

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.example.gnt.characters.di.provider.CharactersDepsStore
import ru.example.gnt.characters.presentation.detials.CharacterDetailsFragment
import ru.example.gnt.characters.presentation.list.CharactersFragment
import ru.example.gnt.common.base.interfaces.RootFragment
import ru.example.gnt.common.base.search.SearchActivity
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.setImageDrawable
import ru.example.gnt.episodes.presentation.episodes.EpisodesFragment
import ru.example.gnt.rickandmorty.databinding.ActivityMainBinding
import ru.example.gnt.rickandmorty.di.main.ActivityComponent
import ru.example.gnt.rickandmorty.di.main.DaggerActivityComponent
import ru.example.gnt.rickandmorty.navigation.MainRouter
import javax.inject.Inject


class MainActivity : AppCompatActivity(), SearchActivity, OnBackStackChangedListener {

    private lateinit var activityComponent: ActivityComponent
    private lateinit var binding: ActivityMainBinding

    private var searchView: SearchView? = null

    @Inject
    lateinit var mainRouter: MainRouter

    private var buttonState: Boolean = false

    private var searchFragment: SearchFragment? = null
    private var searchCloseButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initDagger()
        initActionBar()
        setContentView(binding.root)
        initBottomNav()
        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    private fun initBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                ru.example.gnt.ui.R.id.characters -> {
                    mainRouter.openCharactersScreen()
                }
                ru.example.gnt.ui.R.id.episodes -> {
                    mainRouter.openEpisodesScreen()
                }
                ru.example.gnt.ui.R.id.locations -> {
                    mainRouter.openLocationScreen()
                }
                else -> {
                }
            }
            return@setOnItemSelectedListener true
        }
    }


    private fun initActionBar() {
        mainRouter.openInitialState()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.outlineProvider = null

        with(binding.btnFilter) {
            setOnClickListener {
                (searchView?.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView).callOnClick()
                when (mainRouter.toggleDropDown()) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        searchView?.isVisible = false
                        setImageDrawable(ru.example.gnt.ui.R.drawable.cross_svgrepo_com)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        searchView?.isVisible = true
                        setImageDrawable(ru.example.gnt.ui.R.drawable.baseline_filter_list_24)
                    }
                }
            }
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    fun setToolbarBackButtonVisibility(isVisible: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isVisible)
        supportActionBar?.setDisplayShowHomeEnabled(isVisible)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchFragment?.doSearch(query)
                }
                hideKeyboard(binding.root)
                return true
            }
        })
        return true
    }

    fun setItemsVisibility(isVisible: Boolean) {
        with(binding) {
            btnFilter.isVisible = isVisible
            toolbar.menu?.findItem(ru.example.gnt.ui.R.id.search)?.isVisible = isVisible
        }
    }

    private fun initDagger() {
        activityComponent =
            DaggerActivityComponent.builder()
                .mainContainerId(R.id.main_container)
                .fragmentManager(supportFragmentManager)
                .context(this)
                .build()
        CharactersDepsStore.navigatorDeps = activityComponent
        activityComponent.inject(mainActivity = this)
    }

    override fun showSearchView(isShown: Boolean) {
        binding.toolbar.menu?.findItem(ru.example.gnt.ui.R.id.search)?.isVisible = false
    }

    override fun setSearchText(searchQuery: String) {
        searchView?.setQuery(searchQuery, true)
    }

    override fun registerSearchFragment(instance: SearchFragment) {
        searchFragment = instance
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
        val fragment = mainRouter.getActiveFragment()
        if (fragment is RootFragment) {
            when (fragment) {
                is CharactersFragment -> checkBottomNavSelectedItemId(ru.example.gnt.ui.R.id.characters)
                is CharacterDetailsFragment -> checkBottomNavSelectedItemId(ru.example.gnt.ui.R.id.characters)
                is EpisodesFragment -> checkBottomNavSelectedItemId(ru.example.gnt.ui.R.id.episodes)
            }
        }
    }

}
