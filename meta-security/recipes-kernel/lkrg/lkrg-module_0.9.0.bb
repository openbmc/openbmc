SUMMARY = "Linux Kernel Runtime Guard"
DESCRIPTION="LKRG performs runtime integrity checking of the Linux \
kernel and detection of security vulnerability exploits against the kernel."
SECTION = "security"
HOMEPAGE = "https://www.openwall.com/lkrg/"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d931f44a1f4be309bcdac742d7ed92f9"

DEPENDS = "virtual/kernel elfutils"

SRC_URI = "https://www.openwall.com/lkrg/lkrg-${PV}.tar.gz \
           file://makefile_cleanup.patch "

SRC_URI[sha256sum] = "a997e4d98962c359f3af163bbcfa38a736d2a50bfe35c15065b74cb57f8742bf"

S = "${WORKDIR}/lkrg-${PV}"

inherit module kernel-module-split

MAKE_TARGETS = "modules"

MODULE_NAME = "p_lkrg"

module_do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}
    install -m 0644 ${MODULE_NAME}.ko \
    ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}/${MODULE_NAME}.ko
}

RPROVIDES_${PN} += "kernel-module-lkrg"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"
