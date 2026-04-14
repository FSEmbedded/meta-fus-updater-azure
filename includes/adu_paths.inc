# Central ADU path definitions — single source of truth.
# All recipes that use ADU paths should require this file
# instead of defining paths locally.
#
# Weak assignments (?=) allow distro/machine overrides.

# Configuration directory (on persistent partition, survives OTA)
ADUC_CONF_DIR       ?= "/etc/adu"
ADUC_CERT_DIR       ?= "${ADUC_CONF_DIR}/certs"
ADUC_X509_DIR       ?= "${ADUC_CONF_DIR}/x509_c"
ADUC_DU_CONFIG      ?= "${ADUC_CONF_DIR}/du-config.json"

# Runtime data directory
ADUC_DATA_DIR       ?= "/var/lib/adu"
ADUC_DOWNLOADS_DIR  ?= "/tmp/downloads"
ADUC_EXTENSIONS_DIR ?= "${ADUC_DATA_DIR}/extensions"

# Log directory
ADUC_LOG_DIR        ?= "/var/log/adu"

# Public key directory for update package verification
ADUC_KEY_DIR        ?= "/adukey"
