require conf/machine/include/ieisystem.inc

MACHINE_FEATURES:append = "\
        obmc-chassis-state-mgmt \
        obmc-host-state-mgmt \
        obmc-phosphor-chassis-mgmt \
        "

VIRTUAL-RUNTIME_obmc-host-state-manager ?= "x86-power-control"
VIRTUAL-RUNTIME_obmc-chassis-state-manager ?= "x86-power-control"

PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw ?= "phosphor-ipmi-kcs"
