package com.example.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LawConfirmationDialog
import com.example.ui.components.LawTopBar
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppStrings

@Composable
fun ProfileScreen(
  viewModel: ProfileViewModel,
  currentLanguage: AppLanguage,
  isDarkMode: Boolean,
  onLanguageChange: (AppLanguage) -> Unit,
  onThemeToggle: (Boolean) -> Unit,
  onNavigateToInvoices: () -> Unit,
  onNavigateToDocuments: () -> Unit,
  onNavigateToNotifications: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val user by viewModel.currentUser.collectAsState()
  var showLogoutDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.profileTitle,
        subtitle = strings.firmName,
        isDarkMode = isDarkMode,
        onThemeToggle = onThemeToggle
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Client Identity Card - Classic Chambers Styling
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)),
        shadowElevation = 1.dp
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
              .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(38.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = user?.fullName ?: "Valued Client",
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
            modifier = Modifier.padding(top = 4.dp)
          ) {
            Text(
              text = user?.organization ?: "Corporate Client",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = user?.email ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }
      }

      // 2. Quick Appearance & Theme Card
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 0.5.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Display Theme",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = if (isDarkMode) "Active: Midnight Sovereign (Dark)" else "Active: Oxford Chambers (Light)",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Theme Switcher Segmented Control
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ThemeOptionCard(
              label = "Light Mode",
              icon = Icons.Default.LightMode,
              isSelected = !isDarkMode,
              onClick = { onThemeToggle(false) },
              modifier = Modifier.weight(1f)
            )
            ThemeOptionCard(
              label = "Dark Mode",
              icon = Icons.Default.DarkMode,
              isSelected = isDarkMode,
              onClick = { onThemeToggle(true) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 3. Client Contact Particulars
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 0.5.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = strings.personalInfo,
            style = MaterialTheme.typography.titleSmall.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(12.dp))
          ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = user?.email ?: "—")
          Spacer(modifier = Modifier.height(10.dp))
          ProfileInfoRow(icon = Icons.Default.Phone, label = "Phone", value = user?.phone ?: "—")
          Spacer(modifier = Modifier.height(10.dp))
          ProfileInfoRow(icon = Icons.Default.Business, label = "Organization", value = user?.organization ?: "Individual")
          Spacer(modifier = Modifier.height(10.dp))
          ProfileInfoRow(icon = Icons.Default.LocationOn, label = "Address", value = user?.address ?: "Dar es Salaam, Tanzania")
        }
      }

      // 4. Quick Portal Access Links
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 0.5.dp
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          ProfileNavRow(
            icon = Icons.Default.Receipt,
            title = strings.invoicesTitle,
            subtitle = "Fee notes, receipts & VAT statements",
            onClick = onNavigateToInvoices,
            testTag = "profile_nav_invoices"
          )
          HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          ProfileNavRow(
            icon = Icons.Default.Folder,
            title = strings.documentsTitle,
            subtitle = "Briefs, pleadings & uploaded evidence",
            onClick = onNavigateToDocuments,
            testTag = "profile_nav_documents"
          )
          HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          ProfileNavRow(
            icon = Icons.Default.Notifications,
            title = strings.notificationsTitle,
            subtitle = "Chambers notices & court updates",
            onClick = onNavigateToNotifications,
            testTag = "profile_nav_notifications"
          )
          HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          ProfileNavRow(
            icon = Icons.Default.Settings,
            title = strings.settingsTitle,
            subtitle = "Language, notifications & security",
            onClick = onNavigateToSettings,
            testTag = "profile_nav_settings"
          )
        }
      }

      // 5. Chambers Registry & Contact Directory
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 0.5.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = strings.aboutFirm,
            style = MaterialTheme.typography.titleSmall.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = strings.firmName,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.secondary
            )
          )
          Spacer(modifier = Modifier.height(10.dp))
          ProfileInfoRow(icon = Icons.Default.Place, label = strings.firmAddress, value = "8th Floor, Golden Jubilee Towers, Ohio Street, Dar es Salaam")
          Spacer(modifier = Modifier.height(8.dp))
          ProfileInfoRow(icon = Icons.Default.Call, label = strings.firmPhone, value = "+255 22 212 3456 / +255 754 000 111")
          Spacer(modifier = Modifier.height(8.dp))
          ProfileInfoRow(icon = Icons.Default.AlternateEmail, label = strings.firmEmail, value = "counsel@etcetra.co.tz")
        }
      }

      // 6. Sign Out Button
      OutlinedButton(
        onClick = { showLogoutDialog = true },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("profile_logout_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(strings.logout, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showLogoutDialog) {
    LawConfirmationDialog(
      title = strings.logout,
      message = strings.logoutConfirmation,
      confirmButtonText = strings.logout,
      dismissButtonText = "Cancel",
      isDestructive = true,
      onConfirm = {
        showLogoutDialog = false
        viewModel.logout()
        onLogout()
      },
      onDismiss = { showLogoutDialog = false }
    )
  }
}

@Composable
fun ThemeOptionCard(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderColor by animateColorAsState(
    targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
    label = "border_color"
  )
  val backgroundColor by animateColorAsState(
    targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
    label = "bg_color"
  )

  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(10.dp),
    color = backgroundColor,
    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
    modifier = modifier.height(44.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
    }
  }
}

@Composable
fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
    }
  }
}

@Composable
fun ProfileNavRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(12.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
      Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
  }
}

@Composable
fun SettingsScreen(
  currentLanguage: AppLanguage,
  isDarkMode: Boolean,
  onLanguageChange: (AppLanguage) -> Unit,
  onThemeToggle: (Boolean) -> Unit,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.settingsTitle,
        subtitle = "Preferences & System Configuration",
        showBack = true,
        onBackClick = onNavigateBack,
        isDarkMode = isDarkMode,
        onThemeToggle = onThemeToggle
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Language Configuration
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 0.5.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = strings.language,
            style = MaterialTheme.typography.titleSmall.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ThemeOptionCard(
              label = "English (UK)",
              icon = Icons.Default.Language,
              isSelected = currentLanguage == AppLanguage.ENGLISH,
              onClick = { onLanguageChange(AppLanguage.ENGLISH) },
              modifier = Modifier.weight(1f)
            )

            ThemeOptionCard(
              label = "Kiswahili",
              icon = Icons.Default.Language,
              isSelected = currentLanguage == AppLanguage.SWAHILI,
              onClick = { onLanguageChange(AppLanguage.SWAHILI) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 2. Light / Dark Mode Configuration
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 0.5.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Interface Appearance",
            style = MaterialTheme.typography.titleSmall.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            )
          )
          Text(
            text = "Select your preferred contrast and aesthetic theme",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ThemeOptionCard(
              label = "Light Mode",
              icon = Icons.Default.LightMode,
              isSelected = !isDarkMode,
              onClick = { onThemeToggle(false) },
              modifier = Modifier.weight(1f)
            )

            ThemeOptionCard(
              label = "Dark Mode",
              icon = Icons.Default.DarkMode,
              isSelected = isDarkMode,
              onClick = { onThemeToggle(true) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 3. Security & Legal Notice
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 0.5.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Client Confidentiality & Privilege",
              style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
              )
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "All communications and files exchanged via this client portal are protected under advocate-client privilege under the Laws of Tanzania.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
