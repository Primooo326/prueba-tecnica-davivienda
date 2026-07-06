variable "subscription_id" {
  description = "El ID de la suscripción de Azure donde se desplegarán los recursos"
  type        = string
  # Sin valor por defecto, obligatorio
}

variable "resource_group_name" {
  description = "El nombre del grupo de recursos donde residirán los recursos"
  type        = string
  # Sin valor por defecto, obligatorio
}

variable "container_app_name" {
  description = "El nombre del Azure Container App a desplegar"
  type        = string
  # Sin valor por defecto, obligatorio
}

variable "location" {
  description = "La región de Azure para desplegar los recursos"
  type        = string
  default     = "eastus"
}

variable "environment" {
  description = "El nombre del ambiente (e.g. dev, qa, prod)"
  type        = string
  default     = "dev"
}

variable "acr_name" {
  description = "El nombre del Azure Container Registry (debe ser único globalmente)"
  type        = string
  default     = "acrpruebatecnicabolivar"
}

variable "aca_env_name" {
  description = "El nombre del Container App Environment"
  type        = string
  default     = "cae-prueba-tecnica"
}

variable "log_analytics_name" {
  description = "El nombre del Log Analytics Workspace"
  type        = string
  default     = "log-prueba-tecnica"
}

variable "app_insights_name" {
  description = "El nombre de la instancia de Application Insights"
  type        = string
  default     = "appi-prueba-tecnica"
}

variable "cpu_cores" {
  description = "Número de núcleos CPU para el Container App (mínimo 0.25)"
  type        = string
  default     = "0.25"
}

variable "memory_size" {
  description = "Cantidad de memoria para el Container App (mínimo 0.5Gi)"
  type        = string
  default     = "0.5Gi"
}

variable "container_port" {
  description = "El puerto expuesto por el contenedor de la aplicación"
  type        = number
  default     = 8080
}
