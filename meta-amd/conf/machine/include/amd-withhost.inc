MACHINEOVERRIDES =. "amd-withhost:"

require conf/machine/include/amd.inc

MACHINE_FEATURES:append = "\
        obmc-chassis-state-mgmt \
        obmc-host-state-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-host-ipmi \
        "

VIRTUAL-RUNTIME_obmc-host-state-manager ?= "x86-power-control"
VIRTUAL-RUNTIME_obmc-chassis-state-manager ?= "x86-power-control"

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-amd-apps"
PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw ?= "phosphor-ipmi-kcs"
