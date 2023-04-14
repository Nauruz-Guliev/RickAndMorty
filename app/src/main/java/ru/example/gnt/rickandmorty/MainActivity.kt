package ru.example.gnt.rickandmorty

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import ru.example.gnt.characters.di.provider.CharactersDepsStore
import ru.example.gnt.common.utils.extensions.setImageDrawable
import ru.example.gnt.rickandmorty.databinding.ActivityMainBinding
import ru.example.gnt.rickandmorty.di.main.DaggerMainComponent
import ru.example.gnt.rickandmorty.di.main.MainComponent
import ru.example.gnt.rickandmorty.navigation.Navigator
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private lateinit var mainComponent: MainComponent
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navigator: Navigator

    private var buttonState: Boolean = false

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
                if (navigator.toggleDropDown()) {
                    buttonState = !buttonState
                    when (buttonState) {
                        true -> setImageDrawable(ru.example.gnt.ui.R.drawable.cross_svgrepo_com)
                        false -> setImageDrawable(ru.example.gnt.ui.R.drawable.baseline_filter_list_24)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(ru.example.gnt.ui.R.menu.app_bar_menu, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(ru.example.gnt.ui.R.id.search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@MainActivity, query.toString(), Toast.LENGTH_SHORT).show()
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

}
