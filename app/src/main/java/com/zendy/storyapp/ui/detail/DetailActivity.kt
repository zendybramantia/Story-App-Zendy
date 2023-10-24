package com.zendy.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zendy.storyapp.data.database.StoryEntity
import com.zendy.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<StoryEntity>(EXTRA_STORY) as StoryEntity
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.imgStory)
        binding.tvName.text = story.name
        binding.tvDescription.text = story.description
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}