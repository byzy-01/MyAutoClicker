package com.example.autoclicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PointAdapter : RecyclerView.Adapter<PointAdapter.VH>() {
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvXY = v.findViewById<TextView>(R.id.tvXY)
        val etDelay = v.findViewById<EditText>(R.id.etDelay)
        val etDur = v.findViewById<EditText>(R.id.etDur)
        val etCnt = v.findViewById<EditText>(R.id.etCnt)
        val etRnd = v.findViewById<EditText>(R.id.etRnd)
        val btnDel = v.findViewById<Button>(R.id.btnDel)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        val view = LayoutInflater.from(p.context).inflate(R.layout.item_point, p, false)
        return VH(view)
    }

    override fun getItemCount() = AppState.points.size

    override fun onBindViewHolder(h: VH, i: Int) {
        val p = AppState.points[i]
        h.tvXY.text = "${p.x},${p.y}"
        h.etDelay.setText(p.delayBefore.toString())
        h.etDur.setText(p.pressDuration.toString())
        h.etCnt.setText(p.repeatCount.toString())
        h.etRnd.setText(p.randomOffset.toString())

        h.etDelay.setOnFocusChangeListener { _, f -> if (!f) p.delayBefore = h.etDelay.text.toString().toLongOrNull() ?: 1000 }
        h.etDur.setOnFocusChangeListener { _, f -> if (!f) p.pressDuration = h.etDur.text.toString().toLongOrNull() ?: 50 }
        h.etCnt.setOnFocusChangeListener { _, f -> if (!f) p.repeatCount = h.etCnt.text.toString().toIntOrNull() ?: 1 }
        h.etRnd.setOnFocusChangeListener { _, f -> if (!f) p.randomOffset = h.etRnd.text.toString().toIntOrNull() ?: 0 }

        h.btnDel.setOnClickListener {
            AppState.points.removeAt(i)
            notifyDataSetChanged()
        }
    }
}
