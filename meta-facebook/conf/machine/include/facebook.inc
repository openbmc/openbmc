MACHINE_FEATURES += "\
        obmc-bmc-state-mgmt \
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-flash-mgmt \
        "

VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-fb-apps"
PREFERRED_PROVIDER_virtual/obmc-inventory-data ?= "${VIRTUAL-RUNTIME_skeleton_workbook}"

IMAGE_FEATURES:remove = "obmc-ikvm"

OVERRIDES .= ":facebook"
