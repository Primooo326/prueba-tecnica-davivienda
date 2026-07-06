output "container_app_fqdn" {
  description = "El FQDN público del Azure Container App"
  value       = azurerm_container_app.aca.ingress[0].fqdn
}

output "container_app_url" {
  description = "La URL HTTP de acceso a la aplicación"
  value       = "https://${azurerm_container_app.aca.ingress[0].fqdn}"
}

output "acr_login_server" {
  description = "El servidor de inicio de sesión de Azure Container Registry"
  value       = azurerm_container_registry.acr.login_server
}

output "app_insights_connection_string" {
  description = "La cadena de conexión de Application Insights"
  value       = azurerm_application_insights.appi.connection_string
  sensitive   = true
}
