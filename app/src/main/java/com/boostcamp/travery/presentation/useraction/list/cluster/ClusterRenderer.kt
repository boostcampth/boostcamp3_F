package com.boostcamp.travery.presentation.useraction.list.cluster

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import com.boostcamp.travery.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator


class ClusterRenderer(
        private val context: Context,
        googleMap: GoogleMap,
        clusterManager: ClusterManager<ClusterItemUserAction>
) : DefaultClusterRenderer<ClusterItemUserAction>(context, googleMap, clusterManager) {

    private val dimension = context.resources.getDimension(R.dimen.custom_profile_image).toInt()
    private val padding = context.resources.getDimension(R.dimen.custom_profile_padding).toInt()

    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val multiProfile: View? = inflater.inflate(R.layout.cluster_profile, null)

    private val clusterIconGenerator = IconGenerator(context).apply {
        setContentView(multiProfile)
    }

    private val clusterImageView = multiProfile?.findViewById<ImageView>(R.id.cluster_image)

    private val imageView = ImageView(context).apply {
        layoutParams = ViewGroup.LayoutParams(dimension, dimension)
        setPadding(padding, padding, padding, padding)
    }

    private val iconGenerator = IconGenerator(context).apply {
        setContentView(imageView)
    }

    override fun onBeforeClusterItemRendered(item: ClusterItemUserAction, markerOptions: MarkerOptions) {
        // 이미지가 없을 경우
        if (item.image == "") {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.empty_marker_image))
        } else {
            imageView.setImageDrawable(BitmapDrawable(context.resources, item.image).apply {
                setBounds(0, 0, dimension, dimension)
            })
        }

        val icon = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))?.title(item.name)
    }

    @UiThread
    override fun onBeforeClusterRendered(cluster: Cluster<ClusterItemUserAction>, markerOptions: MarkerOptions) {
        val profilePhotos = ArrayList<Drawable>(Math.min(4, cluster.size))
        val width = dimension
        val height = dimension

        for (p in cluster.items) {
            // 한 클러스터 안에 최대 4개의 이미지를 그림
            if (profilePhotos.size == 4) break
            BitmapDrawable(context.resources, p.image).also {
                it.setBounds(0, 0, width, height)
                profilePhotos.add(it)
            }
        }

        val multiDrawable = MultiDrawable(profilePhotos).apply {
            setBounds(0, 0, width, height)
        }

        clusterImageView?.setImageDrawable(multiDrawable)
        val icon = clusterIconGenerator.makeIcon(cluster.size.toString())
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterItemUserAction>): Boolean {
        return cluster.size > 1
    }
}