require mdio-tools.inc

DEPENDS += "virtual/kernel libmnl"
# This module requires Linux 5.6 higher

S = "${WORKDIR}/git/kernel"

inherit module

EXTRA_OEMAKE = "KDIR=${STAGING_KERNEL_DIR}"
MODULES_INSTALL_TARGET = "install"

RPROVIDES:${PN} += "kernel-module-mdio-netlink"
