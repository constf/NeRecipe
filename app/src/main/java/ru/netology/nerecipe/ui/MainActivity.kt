package ru.netology.nerecipe.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.ActivityMainBinding
import ru.netology.nerecipe.viewModel.RecipesViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val viewModel: RecipesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        binding.bottomNavBar.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(ChangeTitlesAndSuppressUpButton())

    }


    inner class ChangeTitlesAndSuppressUpButton : NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            supportActionBar?.setDisplayShowHomeEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            supportActionBar?.title = when (destination.id) {
                R.id.recipesFeederFragment -> getString(R.string.nav_main_string01)
                R.id.favouriteFeederFragment -> getString(R.string.nav_main_string05)
                R.id.categoriesFeederFragment -> getString((R.string.nav_main_string06))
                R.id.recipesCardFragment -> getString(R.string.recipe_new_string02) + viewModel.showRecipe.value?.name
                R.id.recipeNewFragment -> {
                    val recipe = viewModel.editRecipe.value ?: return
                    if (recipe.name.isBlank() || recipe.name.isEmpty())
                        getString(R.string.recipe_new_string04)
                    else
                        getString(R.string.recipe_new_string05) + recipe.name
                }
                R.id.stepNewFragment -> getString(R.string.nav_main_string04)
                else -> getString(R.string.nav_main_string01)
            }
        }
    }

}