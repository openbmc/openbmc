MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-chassis-state-mgmt \
        obmc-bmc-state-mgmt \
        "
# since we use x86-power-control service, we don't need below three services
VIRTUAL-RUNTIME_obmc-host-state-manager = "x86-power-control"
VIRTUAL-RUNTIME_obmc-chassis-state-manager = "x86-power-control"
VIRTUAL-RUNTIME_obmc-discover-system-state = "x86-power-control"


PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-inventec-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-inventec-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-inventec-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-inventec-apps"
PREFERRED_PROVIDER_virtual/obmc-inventory-data ?= "${VIRTUAL-RUNTIME_skeleton_workbook}"
PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "phosphor-ipmi-kcs"

OVERRIDES .= ":inventec"
