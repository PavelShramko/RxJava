package com.school.rxhomework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.ReplaySubject


class ActivityViewModel : ViewModel() {

    private val disposeBag = CompositeDisposable()

    private val getPost = ReplaySubject.create<List<MainActivity.Adapter.Item>>()

    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    init {
        refreshData()
        getPost.subscribe{
            _state.value = State.Loaded(it)
        }
    }

    private fun refreshData() {
        val result: Disposable = Repository.getPosts()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    response ->
                    if (response.isSuccessful) {
                        response.body()?.let { getPost.onNext(it) }
                    } else {
                        getPost.onNext(emptyList())
                    }
                },{
                    getPost.onNext(emptyList())
                })

        disposeBag.add(result)
    }

    fun unsubscribe(){
        if(disposeBag != null){
            disposeBag.dispose()
        }
    }

    fun processAction(action: Action) {
        when (action) {
            Action.RefreshData -> refreshData()
        }
    }
}
