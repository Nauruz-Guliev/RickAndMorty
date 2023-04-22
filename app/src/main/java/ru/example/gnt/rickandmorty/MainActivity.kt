package ru.example.gnt.rickandmorty

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.example.gnt.characters.di.provider.CharactersDepsStore
import ru.example.gnt.common.base.search.SearchActivity
import ru.example.gnt.common.base.search.SearchFragment
import ru.example.gnt.common.utils.extensions.hideKeyboard
import ru.example.gnt.common.utils.extensions.setImageDrawable
import ru.example.gnt.rickandmorty.databinding.ActivityMainBinding
import ru.example.gnt.rickandmorty.di.main.DaggerMainComponent
import ru.example.gnt.rickandmorty.di.main.MainComponent
import ru.example.gnt.rickandmorty.navigation.Navigator
import javax.inject.Inject


class MainActivity : AppCompatActivity(), SearchActivity {

    private lateinit var mainComponent: MainComponent
    private lateinit var binding: ActivityMainBinding

    private var searchView: SearchView? = null

    @Inject
    lateinit var navigator: Navigator

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
        navigator.openInitialState()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.outlineProvider = null
        with(binding.btnFilter) {
            setOnClickListener {
                when (navigator.toggleDropDown()) {
                    BottomSheetBehavior.STATE_EXPANDED -> setImageDrawable(ru.example.gnt.ui.R.drawable.cross_svgrepo_com)
                    BottomSheetBehavior.STATE_COLLAPSED -> setImageDrawable(ru.example.gnt.ui.R.drawable.baseline_filter_list_24)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(ru.example.gnt.ui.R.menu.app_bar_menu, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(ru.example.gnt.ui.R.id.search)
        searchView = searchItem?.actionView as SearchView

        searchView?.setSearchableInfo(manager.getSearchableInfo(componentName))

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

    private fun initDagger() {
        mainComponent =
            DaggerMainComponent.builder()
                .mainContainerId(R.id.main_container)
                .fragmentManager(supportFragmentManager)
                .context(this)
                .build()
        CharactersDepsStore.navigatorDeps = mainComponent
        mainComponent.inject(mainActivity = this)
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
