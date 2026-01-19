DESCRIPTION = "EIP Kernel Module"
LICENSE = "CLOSED"
PV = "1.0"
LIC_FILES_CHKSUM = "file://../README.md;md5=6f89f1b25e025cd6fffe648d87d06344"

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
SRC_URI = "git://git@sourcevault.axiadord:7999/scm/linux/eip_drv.git;branch=${SRCBRANCH};protocol=ssh"
SRCREV = "77e9780489f64a5fb450f7b985a087d46f297ac0"

# Avoid stripping the .ko file during packaging
INHIBIT_PACKAGE_STRIP = "1"

inherit module

#skip if soc-revision is not revA
python __anonymous () {
    SOC_REV = d.getVar('SOC_REVISION') or ""
    if SOC_REV.strip() == "revA":
        bb.note("Building EIP-driver for revA.")
    else:
        bb.note("Skipping EIP-driver build for revB.")
        raise bb.parse.SkipRecipe("Not reqd for soc revB.")
}

# Module name
MODULE_NAME = "eip_drv"

PROVIDES += "eipdrv-axiado"
RPROVIDES:${PN} += "kernel-module-eip-drv-${KERNEL_VERSION}"

# Shim dependency
DEPENDS += "shimfwl-axiado"
RDEPENDS:${PN} += "kernel-module-shim-hcp"

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

# Point to kernel source and build directory
KERNEL_SRC = "${STAGING_KERNEL_DIR}"
S = "${WORKDIR}/git/axiado_eip"

# Compile against the kernel
do_compile() {
    SHIM_SYMVERS="${RECIPE_SYSROOT}${datadir}/shimfwl/Module.symvers"
    if [ ! -f "${SHIM_SYMVERS}" ]; then
        bbfatal "Missing shim Module.symvers at ${SHIM_SYMVERS}"
    fi

    oe_runmake -C ${STAGING_KERNEL_DIR} \
        M=${S} \
        STAGING_INCDIR=${STAGING_INCDIR} \
        KBUILD_EXTRA_SYMBOLS="${SHIM_SYMVERS}"
}

# Install the .ko to modules path and modules-load.d config
do_install() {
    # Install kernel module
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra

    EIP_KO=$(find ${S} -name "${MODULE_NAME}.ko")
    if [ -z "${EIP_KO}" ]; then
        bbfatal "ERROR: ${MODULE_NAME}.ko not found"
    fi
    install -m 0644 ${EIP_KO} ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    # Generate modules-load.d configuration for automatic loading at boot
    install -d ${D}${sysconfdir}/modules-load.d
    echo "${MODULE_NAME}" > ${D}${sysconfdir}/modules-load.d/eip-drivers.conf
}

FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${MODULE_NAME}.ko"
FILES:${PN} += "${sysconfdir}/modules-load.d/eip-drivers.conf"
