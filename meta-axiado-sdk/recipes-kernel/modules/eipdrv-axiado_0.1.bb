DESCRIPTION = "EIP Kernel Module"
LICENSE = "CLOSED"
PV = "0.1"
LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://eip_drv.ko"

inherit module

RDEPENDS:{PN} += "shimfwl-axiado"

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

# Module name
MODULE_NAME = "eip_drv"

RPROVIDES:${PN} += "kernel-module-eip-drv-${KERNEL_VERSION}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${UNPACKDIR}/eip_drv.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    install -d ${D}${sysconfdir}/modules-load.d
    echo "${MODULE_NAME}" > ${D}${sysconfdir}/modules-load.d/eip-drivers.conf
}

FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${MODULE_NAME}.ko"
FILES:${PN} += "${sysconfdir}/modules-load.d/eip-drivers.conf"
