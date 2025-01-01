package com.snappbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snappbox.ui.R

@Composable
fun DefaultErrorItem(errorMessage: String, retry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = errorMessage,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = retry,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 16.dp)
        ) {
            Text(LocalContext.current.getString(R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDefaultErrorItem() {
    DefaultErrorItem("Something went wrong") {}
}