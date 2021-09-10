# To enable LTO, add following in local.conf
# require conf/distro/include/lto.inc
# DISTRO_FEATURES:append = " lto"
#

# Disable LTO for following packages
LTO:pn-glibc = ""
LTO:pn-gcc-runtime = ""
LTO:pn-libgcc-initial = ""
LTO:pn-libgcc = ""
LTO:pn-libaio = ""
LTO:pn-libpam = ""
LTO:pn-elfutils = ""
LTO:pn-perl = ""
LTO:pn-busybox = ""
LTO:pn-libxcrypt = ""
LTO:pn-curl = ""
LTO:pn-libcap = ""
LTO:pn-libproxy = ""
LTO:pn-libbsd = ""
LTO:pn-perf = ""
# webkit is not linking properly with LTO, disable until next time
LTO:pn-webkitgtk = ""
LTO:pn-xserver-xorg = ""
LTO:pn-grub = ""
LTO:pn-grub-efi = ""

# Custom LTO flags
# disable partitioning/streaming algorithm since its uses ASM
# constructs not compatible with lto
LTOEXTRA:pn-alsa-lib = "-flto-partition=none"

LTOEXTRA ?= ""

# Override it for additional or different options if needed e.g.
# with clang thin-lto might be better for compile speed
#
# ffat-lto-objects
# object files that contain both the intermediate
# language and the object code. This makes them
# usable for both LTO linking and normal linking
#
# -fuse-linker-plugin
# ensures that libraries participate in LTO by supplying intermediate
# code from .a files to linker
LTO ?= "-flto -ffat-lto-objects -fuse-linker-plugin ${LTOEXTRA}"

SELECTED_OPTIMIZATION:append = "${@bb.utils.contains('DISTRO_FEATURES', 'lto', ' ${LTO}', '', d)}"
TARGET_LDFLAGS:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'lto', ' ${LTO}', '', d)}"

SELECTED_OPTIMIZATION[vardeps] += "LTO LTOEXTRA"
