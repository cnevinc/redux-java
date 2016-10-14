package com.android

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
        val OnEditorActionListener: (t: TextView, actionId: Int, event: KeyEvent?) -> Boolean
) : RecyclerView.Adapter<MyViewHolder>(), Subscriber {

    @Inject lateinit var store: Store<AppAction, AppState>
    @Inject lateinit var actionCreator: ActionCreator

    private var subscription: Subscription = Subscription.empty()

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

        if (store.state.editingTodoId == todo.id) {
            animateClickAction(holder.itemView, 100L)

        } else {
            holder.itemView.background = null
        }


    }

    override fun getItemCount() = store.state.list.size

    fun animateClickAction(targetView: View, duration: Long) {
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
        set.addListener(object : AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                actionCreator.edit(null, "")
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }
        })


    }

}



