require wireguard.inc

SRCREV = "fe402261666821514377d06c2c68ed9bc19e7634"

SRC_URI = "git://git.zx2c4.com/wireguard-linux-compat"

inherit module kernel-module-split

DEPENDS = "virtual/kernel libmnl"

# This module requires Linux 3.10 higher and several networking related
# configuration options. For exact kernel requirements visit:
# https://www.wireguard.io/install/#kernel-requirements

EXTRA_OEMAKE_append = " \
    KERNELDIR=${STAGING_KERNEL_DIR} \
    "

MAKE_TARGETS = "module"

RRECOMMENDS_${PN} = "kernel-module-xt-hashlimit"
MODULE_NAME = "wireguard"

module_do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}
    install -m 0644 ${MODULE_NAME}.ko \
    ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}/${MODULE_NAME}.ko
}

# WireGuard has been merged into Linux kernel >= 5.6 and therefore this compatibility module is no longer required.
# OE-core post dunfell has moved to use kernel 5.8 which now means we cant build this module in world builds
# for reference machines e.g. qemu
EXCLUDE_FROM_WORLD = "1"
