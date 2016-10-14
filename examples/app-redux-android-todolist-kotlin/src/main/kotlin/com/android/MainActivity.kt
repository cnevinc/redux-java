package com.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.android.todolist.R
import com.redux.*
import javax.inject.Inject

class MainActivity : BaseActivity(), Subscriber {
    @Inject lateinit var context: Context

    @Inject lateinit var store: Store<AppAction, AppState>
    @Inject lateinit var actionCreator: ActionCreator
    private var subscription: Subscription = Subscription.empty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


    override fun onResume() {
        super.onResume()
        bind()
        subscription = store.subscribe(this)
    }

    override fun onPause() {
        subscription.unsubscribe()
        super.onPause()
    }

    override fun onStateChanged() = bind()

    private fun bind() {

        (findViewById(R.id.todo_count) as TextView?)?.text = "total : ${store.state.list.size}";
        (findViewById(R.id.badge) as TextView?)?.text = "${store.state.list.size}";

    }

    fun todo(view: View) {

        var intent = Intent(this, TodoActivity::class.java)
        startActivity(intent)

    }
}
