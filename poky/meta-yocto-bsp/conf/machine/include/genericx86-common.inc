include conf/machine/include/x86/x86-base.inc
require conf/machine/include/x86/qemuboot-x86.inc
MACHINE_FEATURES += "wifi efi pcbios"

PREFERRED_VERSION_linux-yocto ?= "5.15%"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto"
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
XSERVER ?= "${XSERVER_X86_BASE} \
            ${XSERVER_X86_EXT} \
            ${XSERVER_X86_I915} \
            ${XSERVER_X86_I965} \
            ${XSERVER_X86_FBDEV} \
            ${XSERVER_X86_VESA} \
            ${XSERVER_X86_MODESETTING} \
           "

MACHINE_EXTRA_RRECOMMENDS += "kernel-modules linux-firmware"

GLIBC_ADDONS = "nptl"

EXTRA_OECONF:append:pn-matchbox-panel-2 = " --with-battery=acpi"

IMAGE_FSTYPES += "wic wic.bmap"
WKS_FILE ?= "genericx86.wks.in"
EFI_PROVIDER ??= "grub-efi"
do_image_wic[depends] += "gptfdisk-native:do_populate_sysroot"
do_image_wic[recrdeptask] += "do_bootimg"
