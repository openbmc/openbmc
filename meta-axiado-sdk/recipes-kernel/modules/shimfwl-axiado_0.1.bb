DESCRIPTION = "Axiado Shim and Firewall Layer Kernel Modules"
LICENSE = "CLOSED"
PV = "0.1"
LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://fwl_lin.ko \
           file://shim_hcp.ko \
           "

inherit module

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

SHIM_MODULE_NAME = "shim_hcp"
FWL_MODULE_NAME = "fwl_lin"

RPROVIDES:${PN} += "kernel-module-shim-hcp-${KERNEL_VERSION}"
RPROVIDES:${PN} += "kernel-module-fwl-lin-${KERNEL_VERSION}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra

    install -m 0644 ${UNPACKDIR}/${SHIM_MODULE_NAME}.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/
    install -m 0644 ${UNPACKDIR}/${FWL_MODULE_NAME}.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    install -d ${D}${sysconfdir}/modules-load.d
    echo "${SHIM_MODULE_NAME}" > ${D}${sysconfdir}/modules-load.d/shim-fwl-drivers.conf
    echo "${FWL_MODULE_NAME}" >> ${D}${sysconfdir}/modules-load.d/shim-fwl-drivers.conf
}

FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${SHIM_MODULE_NAME}.ko"
FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${FWL_MODULE_NAME}.ko"
FILES:${PN} += "${sysconfdir}/modules-load.d/shim-fwl-drivers.conf"
