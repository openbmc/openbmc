#@TYPE: Machine
#@NAME: sunray2
#@DESCRIPTION: evaluation machine board configuration for the fsp2 ppc476

PREFERRED_PROVIDER_virtual/kernel ?= "linux-zobmc"

DEFAULTTUNE = "powerpc-nf"

SERIAL_CONSOLES = "115200;ttyS0"

MACHINEOVERRIDES .= ":sunray2"

require conf/machine/include/powerpc/arch-powerpc.inc
