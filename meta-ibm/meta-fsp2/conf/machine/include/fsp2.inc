#@TYPE: Machine
#@NAME: fsp2
#@DESCRIPTION: evaluation machine board configuration for the fsp2 ppc476

PREFERRED_PROVIDER_virtual/kernel ?= "linux-fsp2"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-fsp2"

DEFAULTTUNE = "powerpc-nf"

SERIAL_CONSOLES = "115200;ttyS0"

MACHINEOVERRIDES .= ":fsp2"

require conf/machine/include/powerpc/arch-powerpc.inc
