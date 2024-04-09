MACHINE_FEATURES += "\
    obmc-host-ipmi \
    "

VIRTUAL-RUNTIME_obmc-inventory-manager = "entity-manager"
PREFERRED_PROVIDER_virtual/obmc-inventory-data = "entity-manager"

VIRTUAL-RUNTIME_obmc-sensors-hwmon ?= "dbus-sensors"

VIRTUAL-RUNTIME_obmc-host-state-manager ?= "x86-power-control"
VIRTUAL-RUNTIME_obmc-chassis-state-manager ?= "x86-power-control"

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-asrock-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-asrock-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-asrock-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-asrock-apps"

PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw ?= "phosphor-ipmi-kcs"
