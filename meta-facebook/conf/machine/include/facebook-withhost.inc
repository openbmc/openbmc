MACHINEOVERRIDES =. "fb-withhost:"

require conf/machine/include/facebook.inc

MACHINE_FEATURES:append = "\
        obmc-chassis-state-mgmt \
        obmc-host-state-mgmt \
        obmc-phosphor-chassis-mgmt \
        "

VIRTUAL-RUNTIME_obmc-host-state-manager ?= "x86-power-control"
VIRTUAL-RUNTIME_obmc-chassis-state-manager ?= "x86-power-control"
