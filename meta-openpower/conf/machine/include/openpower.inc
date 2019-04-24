OBMC_MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-host-ipmi \
        obmc-host-ctl \
        obmc-host-state-mgmt \
        obmc-chassis-state-mgmt \
        obmc-bmc-state-mgmt \
        "

# Add OpenPOWER OEM commands handler library
VIRTUAL-RUNTIME_phosphor-ipmi-providers_append_df-openpower = " \
    openpower-ipmi-oem \
"

PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "phosphor-ipmi-bt"
PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-host-ctl ?= "obmc-op-control-host"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-hostfw-config ?= "hostboot-inventory-config-native"
PREFERRED_PROVIDER_virtual/openpower-fru-vpd-layout ?= "openpower-fru-vpd-layout-native"
PREFERRED_PROVIDER_virtual/openpower-fru-inventory ?= "openpower-fru-inventory-example-native"
PREFERRED_PROVIDER_virtual/openpower-fru-properties ?= "openpower-fru-properties-example-native"

# The openpower-occ-control application is data-driven and requires an input
# yaml of OCC instance numbers to sensor IDs.
# This virtual is a native recipe that provides that mapping by installing
# configuration files in the format and native sysroot location expected by
# the openpower-occ-control build process in the native sysroot.
PREFERRED_PROVIDER_virtual/openpower-occ-control-config-native ?= "openpower-occ-control-config-example-native"

#Default value for power supply output power efficiency.
POWER_SUPPLY_DERATING_FACTOR = "90"
