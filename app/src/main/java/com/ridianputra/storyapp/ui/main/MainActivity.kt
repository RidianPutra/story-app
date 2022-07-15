package com.ridianputra.storyapp.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ridianputra.storyapp.data.LoadingStateAdapter
import com.ridianputra.storyapp.R
import com.ridianputra.storyapp.data.preferences.UserPreferences
import com.ridianputra.storyapp.data.preferences.UserViewModel
import com.ridianputra.storyapp.data.preferences.ViewModelFactory
import com.ridianputra.storyapp.databinding.ActivityMainBinding
import com.ridianputra.storyapp.databinding.ItemStoryBinding
import com.ridianputra.storyapp.ui.addstory.AddStoryActivity
import com.ridianputra.storyapp.ui.maps.MapsActivity
import com.ridianputra.storyapp.ui.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var itemBinding: ItemStoryBinding
    private lateinit var adapter: StoriesAdapter
    private lateinit var userViewModel: UserViewModel
    private val factory: com.ridianputra.storyapp.ui.ViewModelFactory =
        com.ridianputra.storyapp.ui.ViewModelFactory.getInstance()
    private val storiesViewModel: StoriesViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        itemBinding = ItemStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_story_menu -> {
                val i = Intent(this, AddStoryActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.story_map -> {
                val i = Intent(this, MapsActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.setting -> {
                binding.progressBar.visibility = View.VISIBLE
                userViewModel.logout()
                return true
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            else -> return true
        }
    }

    private fun setupRecyclerView() {
        adapter = StoriesAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rvStories.setHasFixedSize(true)
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun setupViewModel() {
        userViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[UserViewModel::class.java]

        userViewModel.getUserSession().observe(this) {
            if (it.token.isNullOrBlank()) {
                val i = Intent(this, WelcomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            } else {
                storiesViewModel.getStories(it.token).observe(this) { pagingData ->
                    adapter.submitData(lifecycle, pagingData)
                }
            }
        }
    }
}