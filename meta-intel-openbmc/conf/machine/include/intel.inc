MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-chassis-state-mgmt \
        obmc-bmc-state-mgmt \
        "

VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-intel-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-intel-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-intel-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-intel-apps"
PREFERRED_PROVIDER_virtual/obmc-inventory-data ?= "${VIRTUAL-RUNTIME_skeleton_workbook}"
PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "phosphor-ipmi-kcs"

OVERRIDES .= ":intel"

OBMC_ORG_YAML_SUBDIRS += "com/intel"
