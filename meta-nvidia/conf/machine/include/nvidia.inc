DISTROOVERRIDES .= ":nvidia"

MACHINE_FEATURES += "\
        obmc-bmc-state-mgmt \
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-flash-mgmt \
"

VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-nvidia-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-nvidia-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-nvidia-apps"
PREFERRED_PROVIDER_virtual/obmc-inventory-data ?= "${VIRTUAL-RUNTIME_skeleton_workbook}"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-nvidia-apps"

IMAGE_FEATURES:append = " allow-root-login"

SERIAL_CONSOLES:nvidia = "115200;ttyS4"

