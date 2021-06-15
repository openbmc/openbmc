DEFAULTTUNE ?= "cortexa72-cortexa53"
SOC_FAMILY ?= "zynqmp"

# Available SOC_VARIANT's for zynqmp:
# "cg" - Zynq UltraScale+  CG Devices
# "eg" - Zynq UltraScale+  EG Devices
# "ev" - Zynq UltraScale+  EV Devices
# "dr" - Zynq UltraScale+  DR Devices

SOC_VARIANT ?= "eg"

MACHINEOVERRIDES_prepend_zynqmpeg = "mali400:"
MACHINEOVERRIDES_prepend_zynqmpev = "mali400:vcu:"

require soc-tune-include.inc
require xilinx-soc-family.inc

# Linux Configuration
KERNEL_IMAGETYPE ?= "Image"

# Support multilib on zynqmp
DEFAULTTUNE_virtclass-multilib-lib32 ?= "armv7vethf-neon-vfpv4"

WIC_DEPENDS ?= "virtual/kernel virtual/bootloader virtual/boot-bin virtual/arm-trusted-firmware"

UBOOT_SUFFIX ?= "bin"

XSERVER_EXT_zynqmp ?= "xf86-video-armsoc"
