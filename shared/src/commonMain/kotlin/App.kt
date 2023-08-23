import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.PassportsInteractor
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

private val passportsInteractor by lazy { PassportsInteractor() }

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    Napier.base(DebugAntilog())
    MaterialTheme {
        var greetingText by remember { mutableStateOf("Hello, World!") }
        var showImage by remember { mutableStateOf(false) }
        var startPassportUid by remember { mutableStateOf("2000656012023051400003901"/*"2000656012023081800004122"*/) }
        val localScope = rememberCoroutineScope()
        val passports = passportsInteractor.passports.consumeAsFlow().collectAsState(listOf(), localScope.coroutineContext)
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(startPassportUid, onValueChange = {
                startPassportUid = it
            })
            Button(onClick = {
                greetingText = "Hello, ${getPlatformName()}"
                showImage = !showImage
            }) {
                Text(greetingText)
            }
            Button(onClick = {
                localScope.launch {
                    passportsInteractor.getPassports(startPassportUid)
                }
            }) {
                Text("Никита пидор")
            }
            LazyColumn {
                passports.value.forEach { passport ->
                    item {
                        Column {
                            Text(passport.uid)
                            Row {
                                Text("${passport.internalStatus.percent}%")
                                Spacer(Modifier.size(8.dp))
                                Text(passport.receptionDate)
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(showImage) {
                Image(
                    painterResource("compose-multiplatform.xml"),
                    null
                )
            }
        }
    }
}

expect fun getPlatformName(): String