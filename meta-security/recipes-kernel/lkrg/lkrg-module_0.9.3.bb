SUMMARY = "Linux Kernel Runtime Guard"
DESCRIPTION="LKRG performs runtime integrity checking of the Linux \
kernel and detection of security vulnerability exploits against the kernel."
SECTION = "security"
HOMEPAGE = "https://www.openwall.com/lkrg/"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5105ead24b08a32954f34cbaa7112432"

DEPENDS = "virtual/kernel elfutils"

SRC_URI = "git://github.com/lkrg-org/lkrg.git;protocol=https;branch=main \
           file://makefile_cleanup.patch \
"

SRCREV = "c578e9f786299b67ffd62057b4534b0bf4fb7ece"

S = "${WORKDIR}/git"

inherit module kernel-module-split

MAKE_TARGETS = "modules"

MODULE_NAME = "p_lkrg"

module_do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}
    install -m 0644 ${MODULE_NAME}.ko \
    ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}/${MODULE_NAME}.ko
}

RPROVIDES:${PN} += "kernel-module-lkrg"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"
