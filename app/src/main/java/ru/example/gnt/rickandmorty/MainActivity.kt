package ru.example.gnt.rickandmorty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import ru.example.gnt.characters.di.provider.CharactersDepsStore
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
        binding.btnFilter.setOnClickListener {
            if (navigator.toggleDropDown()) {
                buttonState = !buttonState
                when (buttonState) {
                    true -> {
                        binding.btnFilter.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this,
                                R.drawable.cross_svgrepo_com
                            )
                        )
                    }
                    false -> {
                        binding.btnFilter.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this,
                                ru.example.gnt.ui.R.drawable.settings_icon
                            )
                        )
                    }
                }
            }
        }
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
