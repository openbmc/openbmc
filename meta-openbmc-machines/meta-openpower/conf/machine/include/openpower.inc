OBMC_MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-sensor-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-host-ipmi \
        obmc-host-ctl \
        openpower-pflash \
        "

VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "btbridged"
PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-sensor-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-op-apps"
PREFERRED_PROVIDER_virtual/obmc-host-ctl ?= "obmc-op-control-host"
PREFERRED_PROVIDER_virtual/obmc-inventory-data ?= "${VIRTUAL-RUNTIME_skeleton_workbook}"
