package com.android

import android.content.res.Resources
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.android.todolist.R
import com.redux.ActionCreator
import com.redux.Todo
import kotlinx.android.synthetic.main.todo_header.*
import javax.inject.Inject


class TodoActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    @Inject lateinit var actionCreator: ActionCreator
    private lateinit var adapter: MyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        setupComponents()

    }
    private fun swipeRefreshLayout() = (findViewById(R.id.pull_refresh_container) as SwipeRefreshLayout?)!!

    private fun recyclerView() = (findViewById(R.id.todo_list) as RecyclerView?)!!

    private fun setupComponents() {
        swipeRefreshLayout().setOnRefreshListener(this)
        // setupRecycler()
        adapter = MyAdapter(
                store.state.list,
                resources,
                { compoundButton: CompoundButton, isMarked: Boolean -> actionCreator.complete(compoundButton.tag as Int, isMarked) },
                { v: View -> actionCreator.delete(v.tag as Int) }
        )

        val recyclerView = recyclerView()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //setUpMarkAll
        mark_all.setOnCheckedChangeListener { compoundButton, isChecked -> actionCreator.completeAll(isChecked) }


        // setupEditText() {
        todo_create.setOnEditorActionListener { t: TextView, actionId: Int, event: KeyEvent? ->
            if (event?.action == KeyEvent.ACTION_DOWN && (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)) {
                actionCreator.add(t.text.toString())
                t.text = ""
                t.hideKeyboard()
                true
            } else {
                false
            }
        }

    }


    override fun onRefresh() {
        actionCreator.fetch()
    }

    override fun onResume() {
        super.onResume()
        todo_create.showKeyboard()

    }

    // mapStateToProps ( sate -> {} ) // Subscriber = Connect
    override fun bind() {
        swipeRefreshLayout().isRefreshing = store.state.isFetching

        val todoList = store.state.list

        mark_all.isEnabled = todoList.isEmpty().not()
        mark_all.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean -> }
        mark_all.isChecked = todoList.isEmpty().not() && isAllCompleted(todoList)
        mark_all.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean -> actionCreator.completeAll(isChecked) }

            clear_all_marked.isEnabled = todoList.isEmpty().not() && hasAtLeastOneComplete(todoList)
        adapter.todoList = todoList
        adapter.notifyDataSetChanged()
    }

    private fun hasAtLeastOneComplete(todoList: List<Todo>) = todoList.filter { it.isCompleted == true }.isEmpty().not()

    private fun isAllCompleted(todoList: List<Todo>) = todoList.filter { it.isCompleted == false }.isEmpty()

    private class MyAdapter(
            var todoList: List<Todo>,
            private val resources: Resources,
            private val onMarkedListener: (CompoundButton, Boolean) -> Unit,
            private val onClickDeleteTodo: (View) -> Unit) : RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val todo = todoList.get(position)
            val checkBox = holder.itemView.findViewById(R.id.todo_item) as CheckBox
            checkBox.tag = todo.id
            checkBox.setOnCheckedChangeListener(null)
            checkBox.text = todo.text
            checkBox.isChecked = todo.isCompleted
            checkBox.setTextColor(if (todo.isCompleted) resources.getColor(R.color.task_done) else resources.getColor(R.color.task_todo))
            checkBox.setOnCheckedChangeListener(onMarkedListener)

            val deleteButton = holder.itemView.findViewById(R.id.todo_clear) as Button
            deleteButton.tag = todo.id
            deleteButton.text = if (todo.isCompleted) resources.getString(R.string.clear) else resources.getString(R.string.delete)
            deleteButton.setOnClickListener(onClickDeleteTodo)
        }

        override fun getItemCount() = todoList.size

    }

    private class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
