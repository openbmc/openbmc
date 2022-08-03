SUMMARY = "FF-A Debugfs Linux kernel module"
DESCRIPTION = "This out-of-tree kernel module exposes FF-A operations to user space \
used for development purposes"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://arm_ffa_user.c;beginline=1;endline=1;md5=fcab174c20ea2e2bc0be64b493708266"

SRC_URI = "git://git.gitlab.arm.com/linux-arm/linux-trusted-services.git;branch=main;protocol=https"

# ffa-debugfs v2.1.0
SRCREV = "77967912d033144aff2695cecbd52d3be450deaa"

S = "${WORKDIR}/git"

inherit module

SRC_URI:append = " \
    file://0001-build-add-Yocto-support.patch   \
    file://0002-script-loading-the-driver-in-a-generic-way.patch \
  "

FILES:${PN} += "${bindir}/load_ffa_debugfs.sh"
FILES:${PN}-dev += "${includedir}/arm_ffa_user.h"

do_install:append() {
  install -D -p -m 0755 ${B}/load_ffa_debugfs.sh ${D}/${bindir}/load_ffa_debugfs.sh
  install -m 0644 ${S}/arm_ffa_user.h ${D}/${includedir}/arm_ffa_user.h
}

COMPATIBLE_HOST = "(arm|aarch64).*-linux"

# Kernel modules currently RDEPEND on the kernel, which is troublesome when you want to put a
# kernel module into a initramfs without pulling the kernel into the initramfs, which would be
# silly.  Until this is a recommends the easiest way to handle this is to remove the dependency
# in this recipe.
PACKAGESPLITFUNCS:append = " remove_kernel_dependency"
python remove_kernel_dependency() {
  key = "RDEPENDS:kernel-module-arm-ffa-user-" + d.getVar("KERNEL_VERSION")
  d.delVar(key)
}
