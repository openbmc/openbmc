# Default Xilinx BSP Machine settings

MACHINE_FEATURES_BACKFILL_CONSIDERED += "rtc"

# File System Configuration
IMAGE_FSTYPES ?= "tar.gz cpio cpio.gz.u-boot"

# Kernel Configuration
XILINX_DEFAULT_KERNEL := "linux-xlnx"
XILINX_DEFAULT_KERNEL_microblaze := "linux-yocto"
XILINX_DEFAULT_KERNEL_zynqmp := "linux-yocto"
PREFERRED_PROVIDER_virtual/kernel ??= "${XILINX_DEFAULT_KERNEL}"

# U-Boot Configuration
XILINX_DEFAULT_UBOOT := "u-boot-xlnx"
XILINX_DEFAULT_UBOOT_zynqmp := "u-boot"
PREFERRED_PROVIDER_virtual/bootloader ??= "${XILINX_DEFAULT_UBOOT}"
PREFERRED_PROVIDER_virtual/boot-bin ??= "${PREFERRED_PROVIDER_virtual/bootloader}"

UBOOT_SUFFIX ?= "img"
UBOOT_SUFFIX_zynqmp ?= "bin"
UBOOT_SUFFIX_microblaze ?= "bin"

UBOOT_BINARY ?= "u-boot.${UBOOT_SUFFIX}"
UBOOT_ELF ?= "u-boot"
UBOOT_ELF_aarch64 ?= "u-boot.elf"

XSERVER ?= " \
	xserver-xorg \
	xf86-input-evdev \
	xf86-input-mouse \
	xf86-input-keyboard \
	xf86-video-fbdev \
	${XSERVER_EXT} \
	"

XSERVER_EXT ?= ""
XSERVER_EXT_zynqmp ?= "xf86-video-armsoc"

# For MicroBlaze default all microblaze machines to use GDB 7.7.1 (for gdbserver/gdb)
PREFERRED_VERSION_gdb_microblaze = "7.7.1"

