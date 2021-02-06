package com.school.rxhomework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ActivityViewModel : ViewModel() {

    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val result = Repository.getPosts()
                if (result.isSuccessful) {
                    result.body()?.let { _state.postValue(State.Loaded(it)) }
                }

                // Может ли значение быть не isSuccessful
            } catch (e: Exception){
                _state.postValue(State.Loaded(emptyList()))
            }
        }
    }
}
