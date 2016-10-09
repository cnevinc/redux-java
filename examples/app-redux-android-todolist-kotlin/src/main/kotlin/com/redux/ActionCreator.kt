package com.redux

import javax.inject.Inject

class ActionCreator @Inject constructor(
        private val store: Store<AppAction, AppState>,
        private val operations: Operations) {

    fun fetch(): rx.Subscription {
        store.dispatch(AppAction.Fetching(true))
        return operations
                .fetch()
                .doOnNext { todos -> todos.forEach { store.dispatch(AppAction.Add(it.text, it.isCompleted)) } }
                .doOnCompleted { store.dispatch(AppAction.Fetching(false)) }
                .doOnError { store.dispatch(AppAction.Fetching(false)) }
                .subscribe()
    }

    fun add(text: String) = store.dispatch(AppAction.Add(text, false))

    fun add(text: String, isCompleted: Boolean) = store.dispatch(AppAction.Add(text, isCompleted))

    fun delete(id: Int) = store.dispatch(AppAction.Delete(id))

    fun complete(id: Int, isCompleted: Boolean) = store.dispatch(AppAction.Complete(id, isCompleted))

    fun completeAll(isCompleted: Boolean) = store.dispatch(AppAction.CompleteAll(isCompleted))

    fun clearCompleted() = store.dispatch(AppAction.ClearCompleted)

}
