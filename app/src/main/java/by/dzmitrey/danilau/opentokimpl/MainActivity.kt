package by.dzmitrey.danilau.opentokimpl

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.opentok.android.*
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), Session.SessionListener, PublisherKit.PublisherListener {
    private val API_KEY = "46502162"
    private val SESSION_ID =
        "1_MX40NjUwMjE2Mn5-MTU4MDMyNjk2MDgxNH5RaFg1Rm9SMkpyUUFiZTlGa3VtU3JzWTR-fg"

    private val TOKEN =
        "T1==cGFydG5lcl9pZD00NjUwMjE2MiZzaWc9OTRhMmUyYmEzMDE4MDczZjA5YzNjZDkwMjQ5ODgxZjZiZWUwZTZiOTpzZXNzaW9uX2lkPTFfTVg0ME5qVXdNakUyTW41LU1UVTRNRE15TmprMk1EZ3hOSDVSYUZnMVJtOVNNa3B5VVVGaVpUbEdhM1Z0VTNKeldUUi1mZyZjcmVhdGVfdGltZT0xNTgwMzI2OTYwJnJvbGU9bW9kZXJhdG9yJm5vbmNlPTE1ODAzMjY5NjAuODI3NjQ3MDQ4MzQzMQ=="
    private val LOG_TAG = MainActivity::class.java.simpleName
    private val RC_SETTINGS_SCREEN_PERM = 123
    private val RC_VIDEO_APP_PERM = 124
    lateinit var session: Session
    lateinit var publisher: Publisher
    var subscriber: Subscriber? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun requestPermissions() {
        val perms = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (EasyPermissions.hasPermissions(this, *perms)) {
            session = Session.Builder(this, API_KEY, SESSION_ID).build()
            session.setSessionListener(this)
            session.connect(TOKEN)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera and mic to make video calls",
                RC_VIDEO_APP_PERM,
               *perms
            )
        }
    }


    override fun onConnected(session: Session?) {
        Log.i(LOG_TAG, "Session Connected")
        publisher = Publisher.Builder(this).build()
        publisher.setPublisherListener(this)
        if (publisher.view is GLSurfaceView) {
            (publisher.view as GLSurfaceView).setZOrderOnTop(true)
        }
        session?.publish(publisher)
        publisher_container.addView(publisher.view)
    }

    override fun onDisconnected(session: Session?) {
        Log.i(LOG_TAG, "Session Disconnected")
    }

    override fun onStreamReceived(
        session: Session?,
        stream: Stream?
    ) {
        Log.d(LOG_TAG, "Stream Received")
        Log.i(LOG_TAG, "Stream Received")

        if (subscriber == null) {
            subscriber = Subscriber.Builder(this, stream).build()
            session?.subscribe(subscriber)
            subscriber_container.addView(subscriber?.view)
        }
    }

    override fun onStreamDropped(
        session: Session?,
        stream: Stream?
    ) {
        Log.i(LOG_TAG, "Stream Dropped")
        if (subscriber != null) {
            subscriber = null
            subscriber_container.removeAllViews()
        }
    }

    override fun onError(
        session: Session?,
        opentokError: OpentokError
    ) {
        Log.e(LOG_TAG, "Session error: " + opentokError.message)
    }

    override fun onError(p0: PublisherKit?, p1: OpentokError?) {
        Log.e(LOG_TAG, "Publisher error: " + p1?.message)

    }

    override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {
        Log.i(LOG_TAG, "Publisher onStreamCreated")
    }

    override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed")
    }
}
