package ru.netology.nerecipe.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportFragmentManager.findFragmentByTag(RecipesFeederFragment.TAG) == null) {
            supportFragmentManager.commit {
                add(R.id.app_fragment_container, RecipesFeederFragment(), RecipesFeederFragment.TAG)
            }
        }
    }

}