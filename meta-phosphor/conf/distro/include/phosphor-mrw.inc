DISTRO_FEATURES += "obmc-mrw"
DISTRO_FEATURES_FILTER_NATIVE += "obmc-mrw"
DISTRO_FEATURES_FILTER_NATIVESDK += "obmc-mrw"

DISTROOVERRIDES .= ":df-mrw"

VIRTUAL-RUNTIME_phosphor-hwmon-config:df-mrw ?= "phosphor-hwmon-config-mrw"

PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-inventory:df-mrw ?= "phosphor-ipmi-fru-inventory-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-properties:df-mrw ?= "phosphor-ipmi-fru-properties-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-read-inventory:df-mrw ?= "phosphor-ipmi-fru-read-inventory-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-sensor-inventory:df-mrw ?= "phosphor-ipmi-sensor-inventory-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-inventory-sel:df-mrw ?= "phosphor-ipmi-inventory-sel-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-logging-callouts:df-mrw ?= "phosphor-logging-callouts-mrw-native"
PREFERRED_PROVIDER_virtual/phosphor-fan-presence-config:df-mrw ?= "phosphor-fan-presence-mrw"
PREFERRED_PROVIDER_virtual/phosphor-fan-control-fan-config:df-mrw ?= "phosphor-fan-control-fan-config-mrw"
