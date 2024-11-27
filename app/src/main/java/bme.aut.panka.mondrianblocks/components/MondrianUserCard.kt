package bme.aut.panka.mondrianblocks.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bme.aut.panka.mondrianblocks.ui.theme.MondrianBlue
import bme.aut.panka.mondrianblocks.ui.theme.blue

@Composable
fun MondrianUserCard(
    name: String,
    id: Int,
    birthDate: String,
    isActive: Boolean,
    setIsActive: () -> Unit
) {
    Button(
        onClick = { setIsActive() },
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) MondrianBlue else blue,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "$name ($id)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f, fill = true))
            Text(text = birthDate)
        }
    }
}

@Preview
@Composable
fun PreviewMondrianUserCard() {
    Box(modifier = Modifier.padding(10.dp)) {
        MondrianUserCard(
            name = "John Doe",
            birthDate = "1990-01-01",
            isActive = false,
            setIsActive = { /*TODO*/ },
            id = 1
        )
    }
}