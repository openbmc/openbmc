# To enable LTO, add following in local.conf
# require conf/distro/include/lto.inc
# DISTRO_FEATURES_append = " lto"
#

# Disable LTO for following packages
LTO_pn-glibc = ""
LTO_pn-gcc-runtime = ""
LTO_pn-libgcc-initial = ""
LTO_pn-libgcc = ""
LTO_pn-libaio = ""
LTO_pn-libpam = ""
LTO_pn-elfutils = ""
LTO_pn-perl = ""
LTO_pn-busybox = ""
LTO_pn-libxcrypt = ""
LTO_pn-curl = ""
LTO_pn-libcap = ""
LTO_pn-libproxy = ""
LTO_pn-libbsd = ""
LTO_pn-perf = ""
# webkit is not linking properly with LTO, disable until next time
LTO_pn-webkitgtk = ""
LTO_pn-xserver-xorg = ""

# Custom LTO flags
# disable partitioning/streaming algorithm since its uses ASM
# constructs not compatible with lto
LTOEXTRA_pn-alsa-lib = "-flto-partition=none"

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

SELECTED_OPTIMIZATION_append = "${@bb.utils.contains('DISTRO_FEATURES', 'lto', ' ${LTO}', '', d)}"
TARGET_LDFLAGS_append_class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'lto', ' ${LTO}', '', d)}"

SELECTED_OPTIMIZATION[vardeps] += "LTO LTOEXTRA"
