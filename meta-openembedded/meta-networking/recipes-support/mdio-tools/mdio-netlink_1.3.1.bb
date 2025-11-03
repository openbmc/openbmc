require mdio-tools.inc

DEPENDS += "virtual/kernel libmnl"
# This module requires Linux 5.6 higher

S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE = "-C kernel/ KDIR=${STAGING_KERNEL_DIR}"
MODULES_MODULE_SYMVERS_LOCATION="kernel"
MODULES_INSTALL_TARGET = "install"

RPROVIDES:${PN} += "kernel-module-mdio-netlink"
