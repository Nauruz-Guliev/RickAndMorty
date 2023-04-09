package ru.example.gnt.rickandmorty

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initDagger()
        initActionBar()
        setContentView(binding.root)
        initSearchViewListener()
    }

    private fun initSearchViewListener() {
        val sv = binding.search

        sv.setOnCloseListener {
            val t = Toast.makeText(this@MainActivity, "close", Toast.LENGTH_SHORT)
            t.show()
            false
        }
    }

    private fun initActionBar() {
        navigator.openInitialState()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.outlineProvider = null
        binding.btnFilter.setOnClickListener {
            navigator.toggleDropDown()
        }
    }

    private fun initDagger() {
        mainComponent =
            DaggerMainComponent.builder()
                .mainContainerId(R.id.main_container)
                .fragmentManager(supportFragmentManager)
                .context(this)
                .build()
        mainComponent.inject(mainActivity = this)
    }
}
