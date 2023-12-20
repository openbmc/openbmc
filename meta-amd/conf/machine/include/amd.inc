MACHINE_FEATURES += "\
        obmc-bmc-state-mgmt \
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-flash-mgmt \
        "

VIRTUAL-RUNTIME_obmc-sensors-hwmon ?= "dbus-sensors"
VIRTUAL-RUNTIME_obmc-inventory-manager = "entity-manager"

PREFERRED_PROVIDER_virtual/obmc-inventory-data = "entity-manager"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-amd-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-amd-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-amd-apps"

OVERRIDES .= ":amd"
