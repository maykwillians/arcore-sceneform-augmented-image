package com.example.arcoresceneformaugmentedimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.rendering.ModelRenderable
import java.net.URL

class MainActivity : AppCompatActivity(), Scene.OnUpdateListener {

    private lateinit var arFragment: CustomArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.fragment) as CustomArFragment
        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(null)
        arFragment.arSceneView.planeRenderer.isEnabled = false
        arFragment.arSceneView.scene.addOnUpdateListener(this)
    }

    fun setupDatabase(config: Config, session: Session) {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.caderno)
        val augmentedImageDatabase = AugmentedImageDatabase(session)
        augmentedImageDatabase.addImage("caderno", bitmap, 0.700F)
        config.augmentedImageDatabase = augmentedImageDatabase
    }

    override fun onUpdate(p0: FrameTime?) {
        val frame = arFragment.arSceneView.arFrame
        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(AugmentedImage::class.java)

        for(image in images) {
            if(image.trackingState == TrackingState.TRACKING) {
                if(image.name == "caderno") {
                    val anchor = image.createAnchor(image.centerPose)
                    createModel(anchor)
                }
            }
        }
    }

    private fun createModel(anchor: Anchor) {
        ModelRenderable.Builder()
            .setSource(this, Uri.parse("ArcticFox_Posed.sfb"))
            .build()
            .thenAccept { t: ModelRenderable -> placeModel(t, anchor ) }
    }

    private fun placeModel(modelRenderable: ModelRenderable, anchor: Anchor) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.renderable = modelRenderable
        arFragment.arSceneView.scene.addChild(anchorNode)
    }
}