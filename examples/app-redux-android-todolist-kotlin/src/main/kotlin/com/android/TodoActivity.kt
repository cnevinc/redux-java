package com.android

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.android.todolist.R
import com.redux.*
import javax.inject.Inject


class TodoActivity : BaseActivity(), Subscriber, SwipeRefreshLayout.OnRefreshListener {

    @Inject lateinit var context: Context
    @Inject lateinit var store: Store<AppAction, AppState>
    @Inject lateinit var actionCreator: ActionCreator
    private lateinit var adapter: MyAdapter
    private var subscription: Subscription = Subscription.empty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        swipeRefreshLayout().setOnRefreshListener(this)
        setupRecycler()
        setupEditText()
        setUpMarkAll()
        setupClearAllMarked()
    }

    private fun swipeRefreshLayout() = (findViewById(R.id.pull_refresh_container) as SwipeRefreshLayout?)!!

    private fun recyclerView() = (findViewById(R.id.todo_list) as RecyclerView?)!!

    private fun editText() = (findViewById(R.id.todo_create) as EditText?)!!

    private fun markAllCheckBox() = (findViewById(R.id.mark_all) as CheckBox?)!!

    private fun clearAllMarkedButton() = (findViewById(R.id.clear_all_marked) as Button?)!!

    override fun onRefresh() {
        actionCreator.fetch()
    }

    private fun setUpMarkAll() {
        markAllCheckBox().setOnCheckedChangeListener { compoundButton, isChecked -> actionCreator.completeAll(isChecked) }
    }

    private fun setupEditText() {
        editText().setOnEditorActionListener { t : TextView, actionId : Int, event : KeyEvent? ->
            if (event?.action == KeyEvent.ACTION_DOWN && (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)) {
                actionCreator.add(t.text.toString())
                t.text = ""
                t.hideKeyboard(context)
                true
            } else {
                false
            }
        }
    }

    private fun setupClearAllMarked() {
        clearAllMarkedButton().setOnClickListener { actionCreator.clearCompleted() }
    }

    private fun setupRecycler() {
        adapter = MyAdapter(

                resources,
                { compoundButton: CompoundButton, isMarked: Boolean -> actionCreator.complete(compoundButton.tag as Int, isMarked) },
                { v: View -> actionCreator.delete(v.tag as Int) },
                { t : TextView, actionId : Int, event : KeyEvent? ->
                    Log.d("nevin4","editinging ..$event .$actionId ${t.tag}")
                    if (event?.action == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE ) {
//                        animateClick(t)
                        actionCreator.edit(t.tag as Int,t.text.toString())
                        t.hideKeyboard(context)
                        true
                    } else {
                        false
                    }
                }


        )

        val recyclerView = recyclerView()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun  animateClick(targetView: TextView) {
        val duration = 100L
        val animator1 = ObjectAnimator.ofFloat(targetView, "translationX", 5f)
        animator1.repeatCount = 0
        animator1.duration = duration


        val animator2 = ObjectAnimator.ofFloat(targetView, "translationY", 5f)
        animator2.repeatCount = 0
        animator2.duration = duration

        val animator3 = ObjectAnimator.ofFloat(targetView, "translationX", 0f)
        animator3.repeatCount = 0
        animator3.duration = duration

        val animator4 = ObjectAnimator.ofFloat(targetView, "translationY", 0f)
        animator4.repeatCount = 0
        animator4.duration = duration

        val set = AnimatorSet()
        set.play(animator1).with(animator2)
        set.play(animator1).before(animator3)
        set.play(animator3).with(animator4)

        set.start()
    }


    override fun onResume() {
        super.onResume()

        editText().showKeyboard(context)
        bind()
        subscription = store.subscribe(this)
    }

    override fun onPause() {
        subscription.unsubscribe()
        super.onPause()
    }

    override fun onStateChanged() = bind()

    private fun bind() {

        swipeRefreshLayout().isRefreshing = store.state.isFetching

        val todoList = store.state.list
        val markAllView = markAllCheckBox()
        markAllView.isEnabled = todoList.isEmpty().not()
        markAllView.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean -> }
        markAllView.isChecked = todoList.isEmpty().not() && isAllCompleted(todoList)
        markAllView.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean -> actionCreator.completeAll(isChecked) }
        clearAllMarkedButton().isEnabled = todoList.isEmpty().not() && hasAtLeastOneComplete(todoList)

    }

    private fun hasAtLeastOneComplete(todoList: List<Todo>) = todoList.filter { it.isCompleted == true }.isEmpty().not()

    private fun isAllCompleted(todoList: List<Todo>) = todoList.filter { it.isCompleted == false }.isEmpty()


}
