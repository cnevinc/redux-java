package com.nevinchen.counter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.redux.*
import kotlinx.android.synthetic.main.activity_main.*

data class AppState(val num: Int) : State

sealed class AppAction : Action {

    object Init : AppAction()
    class Add(val num: Int) : AppAction()
    class Minus(val num: Int) : AppAction()
}

val reducer: Reducer<AppAction, AppState> = Reducer({ action: AppAction, state: AppState ->

    when (action) {
        is AppAction.Init -> state
        is AppAction.Add -> state.copy(state.num + 1)
        is AppAction.Minus -> state.copy(state.num - 1)
    }
})

val store: Store<AppAction, AppState> = Store.create<AppAction, AppState>(AppState(0), reducer)

class MainActivity : AppCompatActivity() , Subscriber{

    private var subscription: Subscription = Subscription.empty();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_plus.setOnClickListener{v -> store.dispatch(AppAction.Add(1))};
        bt_minus.setOnClickListener{v -> store.dispatch(AppAction.Minus(1))};

    }

    override fun onResume() {
        super.onResume()
        onStateChanged()
        subscription = store.subscribe(this)
    }

    override fun onPause() {
        subscription.unsubscribe()
        super.onPause()
    }

    override fun onStateChanged() {
        tv_result?.text = store.state.num.toString()
    }

}
