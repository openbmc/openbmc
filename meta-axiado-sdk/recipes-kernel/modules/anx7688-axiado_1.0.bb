DESCRIPTION = "DP ANX7688 Kernel Module"
LICENSE = "CLOSED"
PV = "1.0"
LIC_FILES_CHKSUM = "file://git/LICENSE;md5=fb25944a73e00012e17873c0e8639b81"

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
SRC_URI = "git://git@sourcevault.axiadord:7999/scm/linux/dp_anx7688_drv.git;branch=${SRCBRANCH};protocol=ssh"
SRCREV = "618efc58c46998136ff191c208c6b75a5cb77c42"

# Avoid stripping the .ko file during packaging
INHIBIT_PACKAGE_STRIP = "1"

inherit module

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

#skip if soc-revision is not revA
python __anonymous () {
    SOC_REV = d.getVar('SOC_REVISION') or ""
    if SOC_REV.strip() == "revA":
        bb.note("Building ANX7688-driver for revA.")
    else:
        bb.note("Skipping ANX7688-driver build for revB.")
        raise bb.parse.SkipRecipe("Not reqd for soc revB.")
}

# Module name
MODULE_NAME = "anx7688"

PROVIDES += "anx7688-axiado"
RPROVIDES:${PN} += "kernel-module-anx7688-${KERNEL_VERSION}"

# Point to kernel source and build directory
KERNEL_SRC = "${STAGING_KERNEL_DIR}"
S = "${WORKDIR}/git/drivers/misc/anx7688"

# Compile against the kernel
do_compile() {
    oe_runmake -C ${STAGING_KERNEL_DIR} M=${S}
}

# Install the .ko to modules path and modules-load.d config
do_install() {
    # Install kernel module
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra

    ANX7688_KO=$(find ${S} -name "${MODULE_NAME}.ko")
    if [ -z "${ANX7688_KO}" ]; then
        bbfatal "ERROR: ${MODULE_NAME}.ko not found"
    fi
    install -m 0644 ${ANX7688_KO} ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    # Generate modules-load.d configuration for automatic loading at boot
    install -d ${D}${sysconfdir}/modules-load.d
    echo "${MODULE_NAME}" > ${D}${sysconfdir}/modules-load.d/anx7688-drivers.conf
}

FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/${MODULE_NAME}.ko"
FILES:${PN} += "${sysconfdir}/modules-load.d/anx7688-drivers.conf"
