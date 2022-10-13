MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-chassis-state-mgmt \
        obmc-bmc-state-mgmt \
        "

VIRTUAL-RUNTIME_obmc-host-state-manager ?= "x86-power-control"
VIRTUAL-RUNTIME_obmc-chassis-state-manager ?= "x86-power-control"

PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "phosphor-ipmi-kcs"
PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-hpe-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-hpe-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-hpe-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-hpe-apps"
#PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-hostfw-config ?= "hostboot-inventory-config-native"

IMAGE_CLASSES:append = " hpe-rootfs-postcommands"
