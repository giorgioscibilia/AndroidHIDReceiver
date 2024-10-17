package com.renault.car.androidhidreceiver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.renault.car.androidhidreceiver.ui.theme.AndroidHIDReceiverTheme

class MainActivity : ComponentActivity() {
    import android.bluetooth.BluetoothAdapter
    import android.bluetooth.BluetoothDevice
    import android.bluetooth.BluetoothHidDevice
    import android.bluetooth.BluetoothHidDeviceAppSdpSettings
    import android.bluetooth.BluetoothProfile
    import android.os.Bundle
    import android.util.Log
    import androidx.appcompat.app.AppCompatActivity

    class MainActivity : AppCompatActivity() {

        private val TAG = "HIDReceiver"
        private var bluetoothAdapter: BluetoothAdapter? = null
        private var hidDevice: BluetoothHidDevice? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter?.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                    if (profile == BluetoothProfile.HID_DEVICE) {
                        hidDevice = proxy as BluetoothHidDevice
                        setupHidDevice()
                    }
                }

                override fun onServiceDisconnected(profile: Int) {
                    if (profile == BluetoothProfile.HID_DEVICE) {
                        hidDevice = null
                    }
                }
            }, BluetoothProfile.HID_DEVICE)
        }

        private fun setupHidDevice() {
            val sdpSettings = BluetoothHidDeviceAppSdpSettings(
                "HIDReceiver",
                "HID Receiver",
                "HID",
                BluetoothHidDevice.SUBCLASS1_COMBO,
                null
            )

            val hidCallback = object : BluetoothHidDevice.Callback() {
                override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
                    Log.d(TAG, "HID Device status changed: $registered")
                }

                override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
                    Log.d(TAG, "HID Device connection state changed: $state")
                }

                override fun onGetReport(device: BluetoothDevice?, type: Byte, id: Byte, bufferSize: Int) {
                    Log.d(TAG, "HID Device get report")
                }

                override fun onSetReport(device: BluetoothDevice?, type: Byte, id: Byte, data: ByteArray?) {
                    Log.d(TAG, "HID Device set report: ${data?.toString(Charsets.UTF_8)}")
                }

                override fun onSetProtocol(device: BluetoothDevice?, protocol: Byte) {
                    Log.d(TAG, "HID Device set protocol: $protocol")
                }

                override fun onInterruptData(device: BluetoothDevice?, reportId: Byte, data: ByteArray?) {
                    Log.d(TAG, "HID Device interrupt data: ${data?.toString(Charsets.UTF_8)}")
                }
            }

            val devices = mutableMapOf<BluetoothDevice, Int>()
            hidDevice?.registerApp(sdpSettings, null, null, Runnable::run, hidCallback)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidHIDReceiverTheme {
        Greeting("Android")
    }
}