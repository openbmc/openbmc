# Default Xilinx BSP Machine settings

MACHINE_FEATURES_BACKFILL_CONSIDERED += "rtc"

# File System Configuration
IMAGE_FSTYPES ?= "tar.gz cpio cpio.gz.u-boot"

# Kernel Configuration
PREFERRED_PROVIDER_virtual/kernel ??= "linux-xlnx"

# U-Boot Configuration
PREFERRED_PROVIDER_virtual/bootloader ??= "u-boot-xlnx"
PREFERRED_PROVIDER_virtual/boot-bin ??= "${PREFERRED_PROVIDER_virtual/bootloader}"

do_image_wic[depends] += "${@' '.join('%s:do_deploy' % r for r in (d.getVar('WIC_DEPENDS') or "").split())}"

UBOOT_SUFFIX ?= "img"
UBOOT_SUFFIX_microblaze ?= "bin"

UBOOT_BINARY ?= "u-boot.${UBOOT_SUFFIX}"
UBOOT_ELF ?= "u-boot"
UBOOT_ELF_aarch64 ?= "u-boot.elf"

#Hardware accelaration
PREFERRED_PROVIDER_virtual/libgles1_mali400 = "libmali-xlnx"
PREFERRED_PROVIDER_virtual/libgles2_mali400 = "libmali-xlnx"
PREFERRED_PROVIDER_virtual/egl_mali400 = "libmali-xlnx"
PREFERRED_PROVIDER_virtual/libgl_mali400 = "mesa-gl"
PREFERRED_PROVIDER_virtual/mesa_mali400 = "mesa-gl"

# microblaze does not get on with pie for reasons not looked into as yet
GCCPIE_microblaze = ""
GLIBCPIE_microblaze = ""
SECURITY_CFLAGS_microblaze = ""
SECURITY_LDFLAGS_microblaze = ""
# Microblaze does not support gnu hash style
LINKER_HASH_STYLE_microblaze = "sysv"

XSERVER ?= " \
	xserver-xorg \
	xf86-input-evdev \
	xf86-input-mouse \
	xf86-input-keyboard \
	xf86-video-fbdev \
	${XSERVER_EXT} \
	"

IMAGE_BOOT_FILES ?= "${@get_default_image_boot_files(d)}"

def get_default_image_boot_files(d):
    files = []

    # kernel images
    kerneltypes = set((d.getVar("KERNEL_IMAGETYPE") or "").split())
    kerneltypes |= set((d.getVar("KERNEL_IMAGETYPES") or "").split())
    for i in kerneltypes:
        files.append(i)

    # u-boot image
    if d.getVar("UBOOT_BINARY"):
        files.append(d.getVar("UBOOT_BINARY"))

    # device trees (device-tree only), these are first as they are likely desired over the kernel ones
    if "device-tree" in (d.getVar("MACHINE_ESSENTIAL_EXTRA_RDEPENDS") or ""):
        files.append("devicetree/*.dtb")


    # device trees (kernel only)
    if d.getVar("KERNEL_DEVICETREE"):
        dtbs = d.getVar("KERNEL_DEVICETREE").split(" ")
        dtbs = [os.path.basename(d) for d in dtbs]
        for dtb in dtbs:
            files.append(dtb)

    return " ".join(files)

XSERVER_EXT ?= ""

FPGA_MNGR_RECONFIG_ENABLE ?= "${@bb.utils.contains('IMAGE_FEATURES', 'fpga-manager', '1', '0', d)}"
