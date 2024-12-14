SUMMARY = "Linux Kernel Runtime Guard"
DESCRIPTION="LKRG performs runtime integrity checking of the Linux \
kernel and detection of security vulnerability exploits against the kernel."
SECTION = "security"
HOMEPAGE = "https://www.openwall.com/lkrg/"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;md5=57534ed9f03a5810945cd9be4a81db41"

DEPENDS = "virtual/kernel elfutils"

SRC_URI = "git://github.com/lkrg-org/lkrg.git;protocol=https;branch=main"

SRCREV = "5dc5cfea1f4dc8febdd5274d99e277c17df06acc"

S = "${UNPACKDIR}/git"

inherit module kernel-module-split

MAKE_TARGETS = "modules"

MODULE_NAME = "lkrg"

do_configure:append () {
    sed -i -e 's/^all/modules/' ${S}/Makefile
    sed -i -e 's/^install/modules_install/' ${S}/Makefile
    sed -i -e 's/KERNEL/KERNEL_SRC/g' ${S}/Makefile
}

module_do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}
    install -m 0644 ${MODULE_NAME}.ko \
    ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}/${MODULE_NAME}.ko
}

RPROVIDES:${PN} += "kernel-module-lkrg"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"
