package com.example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import com.example.ui.theme.LawNavyPrimary

@Composable
fun WelcomeScreen(
  onNavigateToLogin: () -> Unit,
  onNavigateToRegister: () -> Unit,
  onQuickDemoAccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Hero image banner
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
    ) {
      Image(
        painter = painterResource(id = R.drawable.law_firm_hero),
        contentDescription = "Et Cetra Law Library",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
              colors = listOf(
                androidx.compose.ui.graphics.Color.Transparent,
                MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                MaterialTheme.colorScheme.background
              )
            )
          )
      )

      // Brand emblem badge
      Surface(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .offset(y = 28.dp)
          .size(72.dp),
        shape = CircleShape,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Gavel,
            contentDescription = null,
            tint = LawGoldSecondary,
            modifier = Modifier.size(36.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(44.dp))

    // Organization & Tagline
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(horizontal = 24.dp)
    ) {
      Text(
        text = strings.firmName,
        style = MaterialTheme.typography.labelLarge.copy(
          letterSpacing = 1.5.sp,
          fontWeight = FontWeight.Bold,
          color = LawGoldSecondary
        ),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = strings.welcomeTitle,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          lineHeight = 32.sp
        ),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = strings.welcomeSubtitle,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 22.sp
        ),
        textAlign = TextAlign.Center
      )
    }

    Spacer(modifier = Modifier.height(36.dp))

    // Actions
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Button(
        onClick = onNavigateToLogin,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("welcome_login_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = strings.signIn, style = MaterialTheme.typography.titleMedium)
      }

      OutlinedButton(
        onClick = onNavigateToRegister,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("welcome_register_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(text = strings.signUp, style = MaterialTheme.typography.titleMedium)
      }

      TextButton(
        onClick = onQuickDemoAccess,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("welcome_demo_button")
      ) {
        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = LawGoldSecondary)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = strings.quickDemoLogin,
          style = MaterialTheme.typography.labelLarge.copy(color = LawGoldSecondary, fontWeight = FontWeight.Bold)
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  viewModel: AuthViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToRegister: () -> Unit,
  onNavigateToForgotPassword: () -> Unit,
  onLoginSuccess: () -> Unit
) {
  val strings = LocalAppStrings.current
  val uiState by viewModel.uiState.collectAsState()

  var email by remember { mutableStateOf("saweanthony9@gmail.com") }
  var password by remember { mutableStateOf("EtCetra@2026") }
  var passwordVisible by remember { mutableStateOf(false) }

  LaunchedEffect(uiState) {
    if (uiState is AuthUiState.Success) {
      onLoginSuccess()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(strings.signIn) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = strings.firmName,
        style = MaterialTheme.typography.labelMedium.copy(
          letterSpacing = 1.2.sp,
          color = LawGoldSecondary,
          fontWeight = FontWeight.Bold
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Access Your Client Portal",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
      )

      Spacer(modifier = Modifier.height(24.dp))

      if (uiState is AuthUiState.Error) {
        Surface(
          color = MaterialTheme.colorScheme.errorContainer,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = (uiState as AuthUiState.Error).message,
              color = MaterialTheme.colorScheme.onErrorContainer,
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }

      OutlinedTextField(
        value = email,
        onValueChange = {
          email = it
          viewModel.clearError()
        },
        label = { Text(strings.email) },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("login_email_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = password,
        onValueChange = {
          password = it
          viewModel.clearError()
        },
        label = { Text(strings.password) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
              imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = null
            )
          }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { viewModel.login(email, password) }),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("login_password_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Box(modifier = Modifier.fillMaxWidth()) {
        TextButton(
          onClick = onNavigateToForgotPassword,
          modifier = Modifier.align(Alignment.CenterEnd)
        ) {
          Text(text = strings.forgotPassword, style = MaterialTheme.typography.labelMedium)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = { viewModel.login(email, password) },
        enabled = uiState !is AuthUiState.Loading && email.isNotBlank() && password.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("login_submit_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        if (uiState is AuthUiState.Loading) {
          CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
          Text(text = strings.signIn, style = MaterialTheme.typography.titleMedium)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      TextButton(onClick = onNavigateToRegister) {
        Text(strings.dontHaveAccount)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
  viewModel: AuthViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToLogin: () -> Unit,
  onRegisterSuccess: () -> Unit
) {
  val strings = LocalAppStrings.current
  val uiState by viewModel.uiState.collectAsState()

  var fullName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var nationalId by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  LaunchedEffect(uiState) {
    if (uiState is AuthUiState.Success) {
      onRegisterSuccess()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(strings.signUp) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Register as an Et Cetra Client",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Enter your verified legal particulars for chambers identification.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      if (uiState is AuthUiState.Error) {
        Surface(
          color = MaterialTheme.colorScheme.errorContainer,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = (uiState as AuthUiState.Error).message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
          )
        }
        Spacer(modifier = Modifier.height(12.dp))
      }

      OutlinedTextField(
        value = fullName,
        onValueChange = { fullName = it },
        label = { Text(strings.fullName) },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_fullname_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text(strings.email) },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_email_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it },
        label = { Text(strings.phoneNumber) },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_phone_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = nationalId,
        onValueChange = { nationalId = it },
        label = { Text(strings.nationalId) },
        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_nid_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(strings.password) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_password_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it },
        label = { Text(strings.confirmPassword) },
        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("register_confirm_password_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          if (password != confirmPassword) {
            // Check mismatch
            return@Button
          }
          viewModel.register(fullName, email, phone, nationalId, password)
        },
        enabled = fullName.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && nationalId.isNotBlank() && password.length >= 6 && password == confirmPassword,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("register_submit_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        if (uiState is AuthUiState.Loading) {
          CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
          Text(text = strings.signUp, style = MaterialTheme.typography.titleMedium)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      TextButton(onClick = onNavigateToLogin) {
        Text(strings.alreadyHaveAccount)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
  viewModel: AuthViewModel,
  onNavigateBack: () -> Unit
) {
  val strings = LocalAppStrings.current
  val uiState by viewModel.uiState.collectAsState()
  var email by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(strings.forgotPassword) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(32.dp))

      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = strings.resetPassword,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = strings.forgotPasswordPrompt,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(28.dp))

      if (uiState is AuthUiState.ResetSent) {
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LawNavyPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Password reset link sent to $email. Please check your inbox.",
              style = MaterialTheme.typography.bodyMedium,
              textAlign = TextAlign.Center
            )
          }
        }
      } else {
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text(strings.email) },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_password_email_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = { viewModel.resetPassword(email) },
          enabled = email.contains("@"),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("forgot_password_submit_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(strings.sendResetLink, style = MaterialTheme.typography.titleMedium)
        }
      }
    }
  }
}
