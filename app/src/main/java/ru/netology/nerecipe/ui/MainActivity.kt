package ru.netology.nerecipe.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR
import androidx.fragment.app.commit
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavBar.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_item_recipes_list -> { // Opens the first and main screen

                    supportFragmentManager.commit {
                        replace(R.id.app_fragment_container, RecipesFeederFragment(), RecipesFeederFragment.TAG)
                    }

//                    if (supportFragmentManager.findFragmentByTag(RecipesFeederFragment.TAG) == null) {
//                        supportFragmentManager.commit {
//                            add(R.id.app_fragment_container, RecipesFeederFragment(), RecipesFeederFragment.TAG)
//                        }
//                    }
//                    else {
//                        supportFragmentManager.commit {
//                            replace(R.id.app_fragment_container, RecipesFeederFragment(), RecipesFeederFragment.TAG)
//                        }
//                    }
                    true
                }

                R.id.menu_item_favourite_list -> { // Opens the screen with favourite recipes
                    binding.bottomNavBar.menu.getItem(1).setChecked(true)
                    supportFragmentManager.commit {
                        //addToBackStack(null)
                        replace(R.id.app_fragment_container, FavouriteFeederFragment(), FavouriteFeederFragment.TAG)
                    }
                    true
                }

                R.id.menu_item_filter_list -> { // Opens the screen with categories to see and choose
                    binding.bottomNavBar.menu.getItem(2).setChecked(true)
                    true
                }

                else -> false
            }

        }

        if (supportFragmentManager.findFragmentByTag(RecipesFeederFragment.TAG) == null) {
            supportFragmentManager.commit {
                add(R.id.app_fragment_container, RecipesFeederFragment(), RecipesFeederFragment.TAG)
            }
        }
    }



}