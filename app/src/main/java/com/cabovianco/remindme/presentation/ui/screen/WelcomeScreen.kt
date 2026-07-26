package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cabovianco.remindme.R
import com.cabovianco.remindme.presentation.ui.screen.shared.BottomActionButton
import com.cabovianco.remindme.presentation.ui.theme.cherryRegular

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomActionButton(
                text = stringResource(R.string.welcome_btn_start),
                onClick = onGetStartedClick,
                paddingValues = PaddingValues(24.dp)
            )
        }
    ) {
        WelcomeContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
}

@Composable
private fun WelcomeContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.common_app_name),
            style = MaterialTheme.typography.displayLarge.copy(fontFamily = cherryRegular)
        )

        Text(
            text = stringResource(R.string.welcome_app_description),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
