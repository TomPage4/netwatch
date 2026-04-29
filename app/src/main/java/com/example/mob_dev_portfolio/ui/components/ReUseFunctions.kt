package com.example.mob_dev_portfolio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.ResolveStatus
import com.example.mob_dev_portfolio.data.entity.RiskRating
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.ui.theme.Green
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.Red
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.example.mob_dev_portfolio.data.constants.ScanType
import com.example.mob_dev_portfolio.ui.theme.Blue
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.Orange
import com.example.mob_dev_portfolio.ui.theme.Pink
import com.example.mob_dev_portfolio.ui.theme.White
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ServiceRow(
    service: ServiceEntity,
    serviceRedirect: (ServiceEntity) -> Unit
) {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = GreyMid,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            }
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    serviceRedirect(service)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val serviceTextStyle = MaterialTheme.typography.bodyMedium
                val riskColor = when (service.riskRating) {
                    RiskRating.LOW -> Green
                    RiskRating.MED -> Orange
                    RiskRating.HIGH -> Red
                    RiskRating.UNRATED -> GreyLight
                }
                val resolveStatusText = when (service.resolveStatus) {
                    ResolveStatus.UNRESOLVED -> "Unresolved"
                    ResolveStatus.PARTIAL -> "Partial"
                    ResolveStatus.RESOLVED -> "Resolved"
                }
                val cardShape = RoundedCornerShape(4.dp)

                Text(text = service.name, style = MaterialTheme.typography.labelMedium)

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RiskBadge(
                        text = "${service.riskRating}",
                        color = riskColor
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = resolveStatusText,
                        style = serviceTextStyle,
                        color = GreyLight
                    )

                    Spacer(Modifier.weight(1f))

                    if (service.isNew) {
                        Spacer(Modifier.width(4.dp))
                        ServiceBadge(text = "new", cardShape = cardShape, color = Green)
                    }
                    if (service.isChanged) {
                        Spacer(Modifier.width(4.dp))
                        ServiceBadge(
                            text = "changed",
                            cardShape = cardShape,
                            color = Orange
                        )
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GreyMid,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun ServiceBadge(
    text: String,
    modifier: Modifier = Modifier,
    cardShape: Shape,
    color: Color
) {
    Card(
        modifier = modifier
            .clip(cardShape)
            .border(
                width = 1.dp,
                color = color,
                shape = cardShape
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = text,
            color = color,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun RiskBadge(
    text: String,
    color: Color,
    big: Boolean = false
) {
    val cardShape = RoundedCornerShape(999.dp)
    Card(
        modifier = Modifier
            .clip(cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.4f)
        )
    ) {
        if (big) {
            Text(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 1.dp
                ),
                text = text,
                color = color,
                style = MaterialTheme.typography.labelMedium
            )
        } else {
            Text(
                modifier = Modifier.padding(
                    horizontal = 6.dp
                ),
                text = text,
                color = color,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun DeviceRow(
    device: DeviceEntity,
    deviceRedirect: (DeviceEntity) -> Unit
) {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = GreyMid,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            }
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { deviceRedirect(device) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val riskColor = when (device.riskRating) {
                    RiskRating.LOW -> Green
                    RiskRating.MED -> Orange
                    RiskRating.HIGH -> Red
                    RiskRating.UNRATED -> GreyLight
                }
                val riskText = when (device.riskRating) {
                    RiskRating.LOW -> "LOW"
                    RiskRating.MED -> "MED"
                    RiskRating.HIGH -> "HIGH"
                    RiskRating.UNRATED -> "Unrated"
                }
                val cardShape = RoundedCornerShape(4.dp)

                Text(text = device.displayName, style = MaterialTheme.typography.labelMedium)

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (device.isTrusted) {
                        RiskBadge(
                            text = "Trusted",
                            color = Blue
                        )
                    } else {
                        RiskBadge(
                            text = "${riskText}",
                            color = riskColor
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "${device.ipAddress}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreyLight
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    when (device.scanType) {
                        ScanType.SERVICE_DISCOVERY -> ServiceBadge(text = "SD", cardShape = cardShape, color = Pink)
                        ScanType.HOST_SCAN -> ServiceBadge(text = "HS", cardShape = cardShape, color = Pink)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    ServiceBadge(
                        text = if (device.serviceCount == 1) {
                            "1 Service"
                        } else {
                            "${device.serviceCount} Services"
                        },
                        cardShape = RoundedCornerShape(4.dp),
                        color = GreyLight
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (device.isNew) {
                        Spacer(modifier = Modifier.width(4.dp))
                        ServiceBadge(text = "new", cardShape = cardShape, color = Green)
                    }

                    if (device.isChanged) {
                        Spacer(Modifier.width(4.dp))
                        ServiceBadge(
                            text = "changed",
                            cardShape = cardShape,
                            color = Orange
                        )
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GreyMid,
                modifier = Modifier
                    .padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun PageHeader(
    heading: String
) {
    Text(
        text = "${heading}",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(6.dp))

    HorizontalDivider(
        modifier = Modifier
            .padding(8.dp),
        1.dp,
        GreyMid
    )
}

@Composable
fun EmptyList(
    title: String,
    image: Int,
    body: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                vertical = 26.dp,
                horizontal = 20.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${title}",
            style = MaterialTheme.typography.labelMedium,
            color = GreyLight
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${body}",
            style = MaterialTheme.typography.bodyMedium,
            color = GreyMid,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InfoRow(
    modifier: Modifier = Modifier,
    title: String,
    data: String,
    dataColor: Color = White,
    isDate: Boolean? = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = GreyMid,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = GreyLight,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isDate == true) {
            val convertDate = LocalDateTime.parse(data)
            Text(
                buildAnnotatedString {
                    append("${convertDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"))}")
                    withStyle(style = SpanStyle(color = GreyLight)) {
                        append(" ${convertDate.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = data,
                color = dataColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}