package com.example.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.model.NotificationType
import com.example.ui.appointments.*
import com.example.ui.auth.*
import com.example.ui.documents.*
import com.example.ui.home.*
import com.example.ui.invoices.*
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppStrings
import com.example.ui.matters.*
import com.example.ui.messages.*
import com.example.ui.notifications.*
import com.example.ui.payment.*
import com.example.ui.profile.*
import com.example.ui.requests.*

data class BottomNavItem(
  val route: String,
  val label: String,
  val icon: ImageVector,
  val testTag: String
)

@Composable
fun AppNavHost(
  navController: NavHostController,
  currentLanguage: AppLanguage,
  isDarkMode: Boolean,
  onLanguageChange: (AppLanguage) -> Unit,
  onThemeToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current

  val authViewModel: AuthViewModel = viewModel()
  val homeViewModel: HomeViewModel = viewModel()
  val requestsViewModel: RequestsViewModel = viewModel()
  val paymentViewModel: PaymentViewModel = viewModel()
  val mattersViewModel: MattersViewModel = viewModel()
  val messagesViewModel: MessagesViewModel = viewModel()
  val appointmentsViewModel: AppointmentsViewModel = viewModel()
  val documentsViewModel: DocumentsViewModel = viewModel()
  val invoicesViewModel: InvoicesViewModel = viewModel()
  val notificationsViewModel: NotificationsViewModel = viewModel()
  val profileViewModel: ProfileViewModel = viewModel()

  val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, strings.navHome, Icons.Default.Home, "bottom_nav_home"),
    BottomNavItem(Screen.Requests.route, strings.navRequests, Icons.Default.Assignment, "bottom_nav_requests"),
    BottomNavItem(Screen.Matters.route, strings.navMatters, Icons.Default.Gavel, "bottom_nav_matters"),
    BottomNavItem(Screen.Messages.route, strings.navMessages, Icons.Default.ChatBubble, "bottom_nav_messages"),
    BottomNavItem(Screen.Appointments.route, strings.navAppointments, Icons.Default.CalendarMonth, "bottom_nav_appointments")
  )

  val showBottomBar = isAuthenticated && bottomNavItems.any { it.route == currentRoute }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    bottomBar = {
      if (showBottomBar) {
        Column {
          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 0.5.dp
          )
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
          ) {
            bottomNavItems.forEach { item ->
              val isSelected = currentRoute == item.route
              NavigationBarItem(
                icon = {
                  Icon(
                    imageVector = item.icon,
                    contentDescription = item.label
                  )
                },
                label = {
                  Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 10.sp
                    )
                  )
                },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = MaterialTheme.colorScheme.secondary,
                  selectedTextColor = MaterialTheme.colorScheme.secondary,
                  indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                  unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                  unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                onClick = {
                  if (currentRoute != item.route) {
                    navController.navigate(item.route) {
                      popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                      }
                      launchSingleTop = true
                      restoreState = true
                    }
                  }
                },
                modifier = Modifier.testTag(item.testTag)
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = if (isAuthenticated) Screen.Home.route else Screen.Welcome.route,
      modifier = Modifier.padding(innerPadding)
    ) {
      // 1. Auth Flow
      composable(Screen.Welcome.route) {
        WelcomeScreen(
          onNavigateToLogin = { navController.navigate(Screen.Login.route) },
          onNavigateToRegister = { navController.navigate(Screen.Register.route) },
          onQuickDemoAccess = {
            authViewModel.login("saweanthony9@gmail.com", "EtCetra@2026")
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Welcome.route) { inclusive = true }
            }
          }
        )
      }

      composable(Screen.Login.route) {
        LoginScreen(
          viewModel = authViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToRegister = { navController.navigate(Screen.Register.route) },
          onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
          onLoginSuccess = {
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Welcome.route) { inclusive = true }
            }
          }
        )
      }

      composable(Screen.Register.route) {
        RegisterScreen(
          viewModel = authViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToLogin = { navController.navigate(Screen.Login.route) },
          onRegisterSuccess = {
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Welcome.route) { inclusive = true }
            }
          }
        )
      }

      composable(Screen.ForgotPassword.route) {
        ForgotPasswordScreen(
          viewModel = authViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 2. Primary Tabs
      composable(Screen.Home.route) {
        HomeScreen(
          viewModel = homeViewModel,
          isDarkMode = isDarkMode,
          onThemeToggle = onThemeToggle,
          onNavigateToMakeRequest = { navController.navigate(Screen.NewRequest.route) },
          onNavigateToBookConsultation = { navController.navigate(Screen.BookConsultation.route) },
          onNavigateToInvoices = { navController.navigate(Screen.Invoices.route) },
          onNavigateToContactOffice = { convId ->
            navController.navigate(Screen.ConversationDetail.createRoute(convId))
          },
          onNavigateToRequestDetails = { reqId ->
            navController.navigate(Screen.RequestDetail.createRoute(reqId))
          },
          onNavigateToMatterDetails = { matId ->
            navController.navigate(Screen.MatterDetail.createRoute(matId))
          },
          onNavigateToPayment = { targetId ->
            navController.navigate(Screen.Payment.createRoute(targetId))
          },
          onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
          onNavigateToAppointments = { navController.navigate(Screen.Appointments.route) }
        )
      }

      composable(Screen.Requests.route) {
        RequestListScreen(
          viewModel = requestsViewModel,
          isDarkMode = isDarkMode,
          onThemeToggle = onThemeToggle,
          onNavigateToNewRequest = { navController.navigate(Screen.NewRequest.route) },
          onNavigateToRequestDetail = { reqId ->
            navController.navigate(Screen.RequestDetail.createRoute(reqId))
          },
          onNavigateToPayment = { targetId ->
            navController.navigate(Screen.Payment.createRoute(targetId))
          }
        )
      }

      composable(Screen.Matters.route) {
        MatterListScreen(
          viewModel = mattersViewModel,
          isDarkMode = isDarkMode,
          onThemeToggle = onThemeToggle,
          onNavigateToMatterDetail = { matId ->
            navController.navigate(Screen.MatterDetail.createRoute(matId))
          },
          onNavigateToNewRequest = { navController.navigate(Screen.NewRequest.route) }
        )
      }

      composable(Screen.Messages.route) {
        ConversationListScreen(
          viewModel = messagesViewModel,
          isDarkMode = isDarkMode,
          onThemeToggle = onThemeToggle,
          onNavigateToConversation = { convId ->
            navController.navigate(Screen.ConversationDetail.createRoute(convId))
          }
        )
      }

      composable(Screen.Appointments.route) {
        AppointmentListScreen(
          viewModel = appointmentsViewModel,
          isDarkMode = isDarkMode,
          onThemeToggle = onThemeToggle,
          onNavigateToBook = { navController.navigate(Screen.BookConsultation.route) },
          onNavigateToDetail = { apptId ->
            navController.navigate(Screen.AppointmentDetail.createRoute(apptId))
          },
          onNavigateToPayment = { targetId ->
            navController.navigate(Screen.Payment.createRoute(targetId))
          }
        )
      }

      // 3. Requests sub-flows
      composable(Screen.NewRequest.route) {
        NewRequestIntakeScreen(
          viewModel = requestsViewModel,
          onNavigateBack = { navController.popBackStack() },
          onSubmissionSuccess = { reqId ->
            navController.navigate(Screen.RequestResult.createRoute(reqId)) {
              popUpTo(Screen.NewRequest.route) { inclusive = true }
            }
          }
        )
      }

      composable(
        route = Screen.RequestDetail.route,
        arguments = listOf(navArgument("requestId") { type = NavType.StringType })
      ) { backStack ->
        val reqId = backStack.arguments?.getString("requestId") ?: ""
        RequestDetailScreen(
          requestId = reqId,
          viewModel = requestsViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToPayment = { id ->
            navController.navigate(Screen.Payment.createRoute(id))
          },
          onNavigateToContactOffice = { convId ->
            navController.navigate(Screen.ConversationDetail.createRoute(convId))
          },
          onNavigateToMatter = { matterId ->
            navController.navigate(Screen.MatterDetail.createRoute(matterId))
          }
        )
      }

      composable(
        route = Screen.RequestResult.route,
        arguments = listOf(navArgument("requestId") { type = NavType.StringType })
      ) { backStack ->
        val reqId = backStack.arguments?.getString("requestId") ?: ""
        RequestSubmissionResultScreen(
          requestId = reqId,
          onNavigateToPayment = { id ->
            navController.navigate(Screen.Payment.createRoute(id))
          },
          onNavigateToRequestDetail = { id ->
            navController.navigate(Screen.RequestDetail.createRoute(id))
          },
          onNavigateToHome = {
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Home.route) { inclusive = true }
            }
          }
        )
      }

      // 4. Payment Flow
      composable(
        route = Screen.Payment.route,
        arguments = listOf(navArgument("paymentId") { type = NavType.StringType })
      ) { backStack ->
        val paymentId = backStack.arguments?.getString("paymentId") ?: ""
        PaymentScreen(
          paymentId = paymentId,
          viewModel = paymentViewModel,
          onNavigateBack = { navController.popBackStack() },
          onPaymentApprovedContinue = {
            navController.popBackStack()
          }
        )
      }

      // 5. Matter details
      composable(
        route = Screen.MatterDetail.route,
        arguments = listOf(navArgument("matterId") { type = NavType.StringType })
      ) { backStack ->
        val matId = backStack.arguments?.getString("matterId") ?: ""
        MatterDetailScreen(
          matterId = matId,
          viewModel = mattersViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToContactCounsel = { convId ->
            navController.navigate(Screen.ConversationDetail.createRoute(convId))
          },
          onNavigateToRequest = { rId ->
            navController.navigate(Screen.RequestDetail.createRoute(rId))
          },
          onNavigateToInvoices = { navController.navigate(Screen.Invoices.route) }
        )
      }

      // 6. Messages Detail
      composable(
        route = Screen.ConversationDetail.route,
        arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
      ) { backStack ->
        val convId = backStack.arguments?.getString("conversationId") ?: ""
        ConversationDetailScreen(
          conversationId = convId,
          viewModel = messagesViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 7. Consultation sub-flow
      composable(Screen.BookConsultation.route) {
        BookConsultationScreen(
          viewModel = appointmentsViewModel,
          onNavigateBack = { navController.popBackStack() },
          onBookSuccess = { appt ->
            navController.navigate(Screen.AppointmentDetail.createRoute(appt.id)) {
              popUpTo(Screen.BookConsultation.route) { inclusive = true }
            }
          }
        )
      }

      composable(
        route = Screen.AppointmentDetail.route,
        arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
      ) { backStack ->
        val apptId = backStack.arguments?.getString("appointmentId") ?: ""
        AppointmentDetailScreen(
          appointmentId = apptId,
          viewModel = appointmentsViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToPayment = { targetId ->
            navController.navigate(Screen.Payment.createRoute(targetId))
          }
        )
      }

      // 8. Documents
      composable(Screen.Documents.route) {
        DocumentListScreen(
          viewModel = documentsViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 9. Invoices
      composable(Screen.Invoices.route) {
        InvoiceListScreen(
          viewModel = invoicesViewModel,
          onNavigateToInvoiceDetail = { invId ->
            navController.navigate(Screen.InvoiceDetail.createRoute(invId))
          },
          onNavigateToPayment = { targetId ->
            navController.navigate(Screen.Payment.createRoute(targetId))
          }
        )
      }

      composable(
        route = Screen.InvoiceDetail.route,
        arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
      ) { backStack ->
        val invId = backStack.arguments?.getString("invoiceId") ?: ""
        InvoiceDetailScreen(
          invoiceId = invId,
          viewModel = invoicesViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToPayment = { targetId ->
            navController.navigate(Screen.Payment.createRoute(targetId))
          }
        )
      }

      // 10. Notifications
      composable(Screen.Notifications.route) {
        NotificationListScreen(
          viewModel = notificationsViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToTarget = { type, targetId ->
            if (!targetId.isNullOrBlank()) {
              when (type) {
                NotificationType.REQUEST -> {
                  val id = targetId.removePrefix("request_details/")
                  navController.navigate(Screen.RequestDetail.createRoute(id))
                }
                NotificationType.PAYMENT -> {
                  val id = targetId.removePrefix("payment_details/")
                  navController.navigate(Screen.Payment.createRoute(id))
                }
                NotificationType.MATTER -> {
                  val id = targetId.removePrefix("matter_details/")
                  navController.navigate(Screen.MatterDetail.createRoute(id))
                }
                NotificationType.APPOINTMENT -> {
                  val id = targetId.removePrefix("appointment_details/")
                  navController.navigate(Screen.AppointmentDetail.createRoute(id))
                }
                NotificationType.MESSAGE -> {
                  val id = targetId.removePrefix("conversation_details/")
                  navController.navigate(Screen.ConversationDetail.createRoute(id))
                }
                NotificationType.DOCUMENT -> {
                  navController.navigate(Screen.Documents.route)
                }
              }
            }
          }
        )
      }

      // 11. Profile & Settings
      composable(Screen.Profile.route) {
        ProfileScreen(
          viewModel = profileViewModel,
          currentLanguage = currentLanguage,
          isDarkMode = isDarkMode,
          onLanguageChange = onLanguageChange,
          onThemeToggle = onThemeToggle,
          onNavigateToInvoices = { navController.navigate(Screen.Invoices.route) },
          onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
          onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
          onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
          onLogout = {
            navController.navigate(Screen.Welcome.route) {
              popUpTo(0) { inclusive = true }
            }
          }
        )
      }

      composable(Screen.Settings.route) {
        SettingsScreen(
          currentLanguage = currentLanguage,
          isDarkMode = isDarkMode,
          onLanguageChange = onLanguageChange,
          onThemeToggle = onThemeToggle,
          onNavigateBack = { navController.popBackStack() }
        )
      }
    }
  }
}
