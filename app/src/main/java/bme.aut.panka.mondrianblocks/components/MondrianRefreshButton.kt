package bme.aut.panka.mondrianblocks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MondrianRefreshButton(
    onClick: () -> Unit,
) {
    Button(onClick = onClick, modifier = Modifier.size(30.dp).background(Color.Transparent)) {
        Icon(
            Icons.Outlined.Refresh,
            contentDescription = "Refresh",
            tint = Color.Black
        )
        Text("asd")
    }
}

@Preview
@Composable
fun PreviewMondrianRefreshButton() {
    MondrianRefreshButton { /*TODO: Handle onClick */ }
}