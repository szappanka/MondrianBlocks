package bme.aut.panka.mondrianblocks.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bme.aut.panka.mondrianblocks.ui.theme.MondrianGray
import bme.aut.panka.mondrianblocks.ui.theme.red

@Composable
fun MondrianButton(
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0),
        colors = ButtonDefaults.buttonColors(containerColor = red, disabledContainerColor = MondrianGray),
        contentPadding = PaddingValues(16.dp),
        enabled = enabled
    ) {
        Text(
            text = text.uppercase(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
