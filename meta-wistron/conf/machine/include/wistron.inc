DISTROOVERRIDES .= ":wistron"

MACHINE_FEATURES += "\
        obmc-bmc-state-mgmt \
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-flash-mgmt \
        "

VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-wistron-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-wistron-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-wistron-apps"
PREFERRED_PROVIDER_virtual/obmc-inventory-data ?= "${VIRTUAL-RUNTIME_skeleton_workbook}"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-wistron-apps"

IMAGE_FEATURES:remove = "obmc-ikvm"

SERIAL_CONSOLES:wistron = "115200;ttyS4"
