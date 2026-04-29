package com.example.mob_dev_portfolio.features.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.mob_dev_portfolio.data.entity.RiskRule
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.White

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RiskRatingRulesScreen(
    settingsViewModel: SettingsViewModel
) {

    val riskRules by settingsViewModel.riskRules.collectAsState()
    val selectedRiskRule by settingsViewModel.selectedRiskRule.collectAsState()

    Scaffold { _ ->
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize()
        ) {
            PageHeader(
                heading = "Risk Rating Rules"
            )

            Spacer(modifier = Modifier.height(16.dp))

            RiskRatingRulesMenu(
                riskRules = riskRules,
                selectedRiskRule = selectedRiskRule,
                onRuleSelect = {
                    settingsViewModel.onRiskRuleSelected(it)
                }
            )
        }
    }
}

@Composable
fun RiskRatingRulesMenu(
    riskRules: List<RiskRule>,
    selectedRiskRule: RiskRule?,
    onRuleSelect: (RiskRule) -> Unit
) {
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                GreyMid,
                cardShape
            )
            .clip(cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            items(
                items = riskRules,
                key = {
                    it
                }
            ) { riskRule ->
                RiskRatingRulesMenuRow(
                    riskRule = riskRule,
                    isSelected = riskRule == selectedRiskRule,
                    onRuleSelect = onRuleSelect
                )
            }
        }
    }
}

@Composable
fun RiskRatingRulesMenuRow(
    riskRule: RiskRule,
    isSelected: Boolean,
    onRuleSelect: (RiskRule) -> Unit
) {

    val riskRuleText = when (riskRule) {
        RiskRule.CONSERVATIVE -> "Conservative"
        RiskRule.STANDARD -> "Standard"
        RiskRule.PERMISSIVE -> "Permissive"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = GreyMid,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            }
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onRuleSelect(riskRule)
                }
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${riskRuleText}", style = MaterialTheme.typography.bodyLarge)
            RadioButton(
                selected = isSelected,
                onClick = {
                    onRuleSelect(riskRule)
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = White,
                    unselectedColor = GreyLight
                )
            )
        }
    }
}