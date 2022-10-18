require wireguard.inc

SRCREV = "18fbcd68a35a892527345dc5679d0b2d860ee004"

SRC_URI = "git://git.zx2c4.com/wireguard-linux-compat;protocol=https;branch=master"

inherit module kernel-module-split

DEPENDS = "virtual/kernel libmnl"

# This module requires Linux 3.10 higher and several networking related
# configuration options. For exact kernel requirements visit:
# https://www.wireguard.io/install/#kernel-requirements

EXTRA_OEMAKE:append = " \
    KERNELDIR=${STAGING_KERNEL_DIR} \
    "

MAKE_TARGETS = "module"
MODULES_INSTALL_TARGET = "module-install"

RRECOMMENDS:${PN} = "kernel-module-xt-hashlimit"
MODULE_NAME = "wireguard"


# WireGuard has been merged into Linux kernel >= 5.6 and therefore this compatibility module is no longer required.
# OE-core post dunfell has moved to use kernel 5.8 which now means we cant build this module in world builds
# for reference machines e.g. qemu
EXCLUDE_FROM_WORLD = "1"

