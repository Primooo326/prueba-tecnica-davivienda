terraform {
  required_version = ">= 1.5.0"

  backend "azurerm" {
    resource_group_name  = "rg-prueba-tecnica"
    storage_account_name = "stpruebatecnicabolivar"
    container_name       = "tfstate"
    key                  = "prueba-tecnica-bolivar-states"
    # El token SAS se debe pasar usando -backend-config="sas_token=<SAS_TOKEN>"
  }
}
