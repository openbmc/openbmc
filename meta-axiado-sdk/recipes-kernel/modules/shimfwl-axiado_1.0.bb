DESCRIPTION = "Axiado Shim and Firewall Layer Kernel Modules"
LICENSE = "CLOSED"
PV = "1.0"

LIC_FILES_CHKSUM = "file://../README.md;md5=6f89f1b25e025cd6fffe648d87d06344"

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
SRC_URI = "git://git@sourcevault.axiadord:7999/scm/linux/shim_fwl_drv.git;branch=${SRCBRANCH};protocol=ssh"
SRCREV = "da6ccfaf339ac3cc177d7309713ce54c34cb4297"

INHIBIT_PACKAGE_STRIP = "1"
inherit module

python __anonymous () {
    SOC_REV = d.getVar('SOC_REVISION') or ""
    if SOC_REV.strip() == "revA":
        bb.note("Building Shim+FWL drivers for revA.")
    else:
        bb.note("Skipping Shim+FWL drivers build for revB.")
        raise bb.parse.SkipRecipe("Not required for soc revB.")
}

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

SHIM_MODULE_NAME = "shim_hcp"
FWL_MODULE_NAME = "fwl_lin"

PROVIDES += "shimfwl-axiado"
RPROVIDES:${PN} += "kernel-module-shim-hcp-${KERNEL_VERSION}"
RPROVIDES:${PN} += "kernel-module-fwl-lin-${KERNEL_VERSION}"
RPROVIDES:${PN} += "kernel-module-shim-hcp"
RPROVIDES:${PN} += "kernel-module-fwl-lin"

KERNEL_SRC = "${STAGING_KERNEL_DIR}"
S = "${WORKDIR}/git"
AXIADO_DRV_PATH = "${S}/drivers/net/ethernet/axiado"

do_compile() {
    oe_runmake -C ${KERNEL_SRC} M=${AXIADO_DRV_PATH}
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra

    SHIM_KO=$(find ${AXIADO_DRV_PATH} -name "${SHIM_MODULE_NAME}.ko")
    install -m 0644 ${SHIM_KO} ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    FWL_KO=$(find ${AXIADO_DRV_PATH} -name "${FWL_MODULE_NAME}.ko")
    install -m 0644 ${FWL_KO} ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    # Install headers for dependent drivers
    install -d ${D}${includedir}/axiado/shim_driver
    if [ -d "${AXIADO_DRV_PATH}/shim_driver/src" ]; then
        find ${AXIADO_DRV_PATH}/shim_driver/src -name "*.h" \
            -exec install -m 0644 {} ${D}${includedir}/axiado/shim_driver/ \;
    fi

    # Install Module.symvers for symbol resolution
    install -d ${D}${datadir}/shimfwl
    if [ -f "${AXIADO_DRV_PATH}/Module.symvers" ]; then
        install -m 0644 ${AXIADO_DRV_PATH}/Module.symvers ${D}${datadir}/shimfwl/Module.symvers
    else
        bbwarn "Module.symvers not found at ${AXIADO_DRV_PATH}/Module.symvers"
    fi

    # Auto-load configuration
    install -d ${D}${sysconfdir}/modules-load.d
    echo "${SHIM_MODULE_NAME}" > ${D}${sysconfdir}/modules-load.d/shim-fwl-drivers.conf
    echo "${FWL_MODULE_NAME}" >> ${D}${sysconfdir}/modules-load.d/shim-fwl-drivers.conf
}

FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${SHIM_MODULE_NAME}.ko"
FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${FWL_MODULE_NAME}.ko"
FILES:${PN} += "${sysconfdir}/modules-load.d/shim-fwl-drivers.conf"
FILES:${PN}-dev += "${includedir}/axiado/*"
FILES:${PN}-dev += "${datadir}/shimfwl/Module.symvers"
