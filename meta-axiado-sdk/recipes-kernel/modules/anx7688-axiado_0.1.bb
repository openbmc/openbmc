DESCRIPTION = "DP ANX7688 Kernel Module"
LICENSE = "CLOSED"
PV = "0.1"
LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://anx7688.ko"

inherit module

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

MODULE_NAME = "anx7688"

RPROVIDES:${PN} += "kernel-module-anx7688-${KERNEL_VERSION}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${UNPACKDIR}/${MODULE_NAME}.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    install -d ${D}${sysconfdir}/modules-load.d
    echo "${MODULE_NAME}" > ${D}${sysconfdir}/modules-load.d/anx7688-drivers.conf
}

FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${MODULE_NAME}.ko"
FILES:${PN} += "${sysconfdir}/modules-load.d/anx7688-drivers.conf"
