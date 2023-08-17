MACHINEOVERRIDES =. "fb-compute-singlehost:"

require conf/machine/include/facebook-compute.inc

MACHINE_FEATURES:append = " obmc-host-ipmi"
PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw ?= "phosphor-ipmi-kcs"
