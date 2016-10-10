package com.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.redux.*
import javax.inject.Inject

public open class BaseActivity : AppCompatActivity() , Subscriber {
    @Inject lateinit var presenter: MainPresenter
    @Inject lateinit var store: Store<AppAction, AppState>

    private var subscription: Subscription = Subscription.empty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Application.getObjectGraph().inject(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        presenter.bind(this)
    }

    override fun onStateChanged() = bind()

    override fun onResume() {
        super.onResume()
        bind()
        subscription = store.subscribe(this)
    }

    override fun onPause() {
        subscription.unsubscribe()
        super.onPause()
    }

    open fun bind() {
    }
}
