package com.etaratasy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.data.Notification
import com.etaratasy.app.data.TypeNotification
import com.etaratasy.app.ui.components.EcranHeader
import com.etaratasy.app.ui.theme.EtataColors

@Composable
fun NotificationsScreen(
    notifications: List<Notification>,
    onRetour: () -> Unit,
    onLue: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item { EcranHeader(titre = "Notifications", onRetour = onRetour) }

        items(notifications, key = { it.id }) { n ->
            val icone = when (n.type) {
                TypeNotification.MAJORITE -> Icons.Default.Badge
                TypeNotification.PERMIS -> Icons.Default.DirectionsCar
                TypeNotification.RENDEZ_VOUS -> Icons.Default.Event
                TypeNotification.MISE_A_JOUR -> Icons.Default.EditNote
            }
            Box(Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (n.lue) EtataColors.Surface else EtataColors.RougeSoft,
                    border = BorderStroke(1.dp, if (n.lue) EtataColors.Line else EtataColors.RougeSoft),
                    modifier = Modifier.fillMaxWidth().clickable { onLue(n.id) }
                ) {
                    Row(Modifier.padding(16.dp)) {
                        Icon(
                            icone, null,
                            tint = if (n.lue) EtataColors.InkSoft else EtataColors.Rouge,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(n.titre, fontSize = 14.sp, color = EtataColors.Ink, lineHeight = 19.sp)
                            Spacer(Modifier.height(5.dp))
                            Text(n.corps, fontSize = 13.sp, color = EtataColors.InkSoft, lineHeight = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(n.quand, fontSize = 11.sp, color = EtataColors.InkSoft)
                        }
                        if (!n.lue) {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = EtataColors.Rouge,
                                modifier = Modifier.size(8.dp).align(Alignment.Top)
                            ) {}
                        }
                    }
                }
            }
        }
    }
}
