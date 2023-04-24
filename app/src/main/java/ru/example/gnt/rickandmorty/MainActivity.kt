package ru.example.gnt.rickandmorty

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.example.gnt.characters.di.provider.CharactersDepsStore
import ru.example.gnt.common.base.search.SearchActivity
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.setImageDrawable
import ru.example.gnt.rickandmorty.databinding.ActivityMainBinding
import ru.example.gnt.rickandmorty.di.main.ActivityComponent
import ru.example.gnt.rickandmorty.di.main.DaggerActivityComponent
import ru.example.gnt.rickandmorty.navigation.MainRouter
import javax.inject.Inject


class MainActivity : AppCompatActivity(), SearchActivity {

    private lateinit var activityComponent: ActivityComponent
    private lateinit var binding: ActivityMainBinding

    private var searchView: SearchView? = null

    @Inject
    lateinit var mainRouter: MainRouter

    private var buttonState: Boolean = false

    private var searchFragment: SearchFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initDagger()
        initActionBar()
        setContentView(binding.root)
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


        val closeButton: ImageView =
            searchView?.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView

        closeButton.setOnClickListener {
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
        val status = if (isVisible) ViewGroup.VISIBLE else ViewGroup.INVISIBLE

        with(binding) {
            btnFilter.isVisible = isVisible
            toolbar.menu?.findItem(ru.example.gnt.ui.R.id.search)?.isVisible = isVisible
            //    bottomNav.isVisible = isVisible
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

}
