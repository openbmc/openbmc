OBMC_MACHINE_FEATURES += "\
        obmc-bmc-state-mgmt \
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-flash-mgmt \
        "

VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER:virtual/obmc-chassis-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER:virtual/obmc-fan-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER:virtual/obmc-flash-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER:virtual/obmc-inventory-data ?= "${VIRTUAL-RUNTIME_skeleton_workbook}"
PREFERRED_PROVIDER:virtual/obmc-system-mgmt = "packagegroup-fb-apps"

IMAGE_FEATURES:remove = "obmc-ikvm"

OVERRIDES .= ":facebook"
