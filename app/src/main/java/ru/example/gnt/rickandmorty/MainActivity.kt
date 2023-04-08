package ru.example.gnt.rickandmorty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        setContentView(binding.root)
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
