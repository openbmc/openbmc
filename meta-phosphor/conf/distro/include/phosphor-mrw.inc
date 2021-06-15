DISTRO_FEATURES += "obmc-mrw"
DISTRO_FEATURES_FILTER_NATIVE += "obmc-mrw"
DISTRO_FEATURES_FILTER_NATIVESDK += "obmc-mrw"

DISTROOVERRIDES .= ":df-mrw"

VIRTUAL-RUNTIME_phosphor-hwmon-config_df-mrw ?= "phosphor-hwmon-config-mrw"

PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-inventory_df-mrw ?= "phosphor-ipmi-fru-inventory-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-properties_df-mrw ?= "phosphor-ipmi-fru-properties-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-read-inventory_df-mrw ?= "phosphor-ipmi-fru-read-inventory-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-sensor-inventory_df-mrw ?= "phosphor-ipmi-sensor-inventory-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-inventory-sel_df-mrw ?= "phosphor-ipmi-inventory-sel-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-led-manager-config-native_df-mrw ?= "phosphor-led-manager-config-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-logging-callouts_df-mrw ?= "phosphor-logging-callouts-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-fan-presence-config_df-mrw ?= "phosphor-fan-presence-mrw"
PREFERRED_PROVIDER_virtual/phosphor-fan-control-fan-config_df-mrw ?= "phosphor-fan-control-fan-config-mrw"
