
VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "phosphor-ipmi-bt"
PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-arm-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-arm-apps"
PREFERRED_PROVIDER_virtual/obmc-host-ctl ?= "obmc-op-control-host"
PREFERRED_PROVIDER_virtual/obmc-inventory-data ?= \
    "${VIRTUAL-RUNTIME_skeleton_workbook}"
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-hostfw-config ?= \
    "hostboot-inventory-config-native"
PREFERRED_PROVIDER_virtual/openpower-fru-vpd-layout ?= \
    "openpower-fru-vpd-layout-native"
PREFERRED_PROVIDER_virtual/openpower-fru-inventory ?= \
    "openpower-fru-inventory-example-native"
PREFERRED_PROVIDER_virtual/openpower-fru-properties ?= \
    "openpower-fru-properties-example-native"
