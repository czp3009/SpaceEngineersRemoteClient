package com.hiczp.spaceengineers.remoteclient.android.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hiczp.spaceengineers.remoteclient.android.*
import com.hiczp.spaceengineers.remoteclient.android.adapter.ProfileListAdapter
import com.hiczp.spaceengineers.remoteclient.android.layout.defaultStyle
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {
    private lateinit var model: ProfileListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this)[ProfileListViewModel::class.java]

        lateinit var createButton: ImageButton
        lateinit var recycleView: RecyclerView
        verticalLayout {
            appBarLayout {
                toolbar {
                    defaultStyle()
                    createButton = imageButton(R.drawable.ic_add_white_24dp) {
                        backgroundColor = Color.TRANSPARENT
                    }
                }
            }

            recycleView = recyclerView {
                layoutManager = LinearLayoutManager(context)
            }
        }

        model.creating.observe(this) {
            createButton.isEnabled = !it
        }
        createButton.onClick {
            model.creating.value = true
            startActivityForResult<ProfileActivity>(0)
        }

        val profileListAdapter = ProfileListAdapter(
            model.opening,
            onDelete = { model.delete(it) },
            onOpen = { startActivity<VRageActivity>(VRageActivity.inputValue to it) },
            onModify = {
                startActivityForResult<ProfileActivity>(
                    0,
                    ProfileActivity.inputValue to it
                )
            }
        )
        recycleView.adapter = profileListAdapter
        model.profiles.observe(this) {
            profileListAdapter.setProfiles(it)
        }
    }

    override fun onResume() {
        super.onResume()
        model.opening.postValue(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        model.creating.postValue(false)
        if (resultCode != Activity.RESULT_OK) return

        val newProfile = data!!.extras!!.get(ProfileActivity.returnValue) as Profile
        model.save(newProfile)
    }
}

class ProfileListViewModel : ViewModel() {
    val profiles: MutableLiveData<List<Profile>> = MutableLiveData()
    val creating = MutableLiveData(false)
    val opening = MutableLiveData(false)

    init {
        pullData()
    }

    private fun pullData() {
        profiles.postValue(database.use { findAll() })
    }

    fun save(profile: Profile) {
        database.use { save(profile) }
        pullData()
    }

    fun delete(profile: Profile) {
        database.use { deleteById(profile.id!!) }
        pullData()
    }
}
