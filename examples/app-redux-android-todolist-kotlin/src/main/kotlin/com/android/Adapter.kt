package com.android

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.android.todolist.R
import com.redux.*
import javax.inject.Inject

/**
 * Created by nevin on 10/12/16.
 */

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class MyAdapter(

        val resources: Resources,
        val onMarkedListener: (CompoundButton, Boolean) -> Unit,
        val onClickDeleteTodo: (View) -> Unit,
        val OnEditorActionListener:(t : TextView, actionId : Int, event : KeyEvent?) -> Boolean
) : RecyclerView.Adapter<MyViewHolder>() , Subscriber {

    @Inject lateinit var store: Store<AppAction, AppState>
    private var subscription: Subscription = Subscription.empty();

    init {
        Application.getObjectGraph().inject(this)
    }

    override fun onStateChanged() {
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        subscription = store.subscribe(this)
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val todo = store.state.list.get(position)
        val checkBox = holder.itemView.findViewById(R.id.todo_item) as CheckBox
        checkBox.tag = todo.id
        checkBox.setOnCheckedChangeListener(null)

        checkBox.isChecked = todo.isCompleted
        checkBox.setTextColor(if (todo.isCompleted) resources.getColor(R.color.task_done) else resources.getColor(R.color.task_todo))
        checkBox.setOnCheckedChangeListener(onMarkedListener)

        val deleteButton = holder.itemView.findViewById(R.id.todo_clear) as Button
        deleteButton.tag = todo.id
        deleteButton.text = if (todo.isCompleted) resources.getString(R.string.clear) else resources.getString(R.string.delete)
        deleteButton.setOnClickListener(onClickDeleteTodo)

        val content = holder.itemView.findViewById(R.id.todo_content) as TextView
        content.text = todo.text
        content.tag = todo.id
        content.setOnEditorActionListener(OnEditorActionListener)

        if (store.state.editingTodoId == todo.id){
            holder.itemView.setBackgroundResource(android.R.color.holo_red_dark)
        }


    }

    override fun getItemCount() = store.state.list.size

}


