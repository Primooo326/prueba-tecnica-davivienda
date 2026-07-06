provider "azurerm" {
  features {}
  subscription_id = var.subscription_id
}

# Grupo de Recursos
resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location
}

# Azure Container Registry (ACR) básico
resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

# Log Analytics Workspace (requerido para ACA y App Insights)
resource "azurerm_log_analytics_workspace" "law" {
  name                = var.log_analytics_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# Application Insights
resource "azurerm_application_insights" "appi" {
  name                = var.app_insights_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  workspace_id        = azurerm_log_analytics_workspace.law.id
  application_type    = "web"
}

# Container App Environment
resource "azurerm_container_app_environment" "cae" {
  name                       = var.aca_env_name
  resource_group_name        = azurerm_resource_group.rg.name
  location                   = azurerm_resource_group.rg.location
  log_analytics_workspace_id = azurerm_log_analytics_workspace.law.id
}

# Azure Container App
resource "azurerm_container_app" "aca" {
  name                         = var.container_app_name
  container_app_environment_id = azurerm_container_app_environment.cae.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  # Credenciales para descargar la imagen privada desde nuestro ACR
  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  template {
    container {
      name   = "app-gestion-polizas"
      image  = "${azurerm_container_registry.acr.login_server}/gestion-polizas:latest"
      cpu    = var.cpu_cores
      memory = var.memory_size

      # Cadena de conexión para habilitar la telemetría en el SDK de Spring Boot
      env {
        name  = "APPLICATIONINSIGHTS_CONNECTION_STRING"
        value = azurerm_application_insights.appi.connection_string
      }

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }
    }
  }

  # Configuración de red e ingreso público a internet
  ingress {
    allow_insecure_connections = false
    external_enabled           = true
    target_port                = var.container_port
    
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }
}
