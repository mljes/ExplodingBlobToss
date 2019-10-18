package com.happycampers.explodingblobtoss.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happycampers.explodingblobtoss.R
import com.happycampers.explodingblobtoss.model.WifiDirectPeer
import kotlinx.android.synthetic.main.peer.view.*

class PeerListAdapter (
    private val context: Context,
    private val peers: List<WifiDirectPeer>
): RecyclerView.Adapter<PeerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.peer, viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = peers.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var peer = peers[position]

        viewHolder.peerItem.setOnClickListener {
            //TODO: this will need to be set to the connection method
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val peerItem: CardView
            get() = itemView.itemLayout
    }

}