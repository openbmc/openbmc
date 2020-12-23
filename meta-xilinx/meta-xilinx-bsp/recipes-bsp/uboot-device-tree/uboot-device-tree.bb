SUMMARY = "Xilinx BSP u-boot device trees"
DESCRIPTION = "Xilinx BSP u-boot device trees from within layer."
SECTION = "bsp"

LICENSE = "MIT & GPLv2"
LIC_FILES_CHKSUM = " \
                file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6 \
                "

inherit devicetree xsctdt xsctyaml


REPO ??= "git://github.com/xilinx/device-tree-xlnx.git;protocol=https"
BRANCH ??= "master"
BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH') != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

PROVIDES = "virtual/uboot-dtb"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

SRCREV ??= "bc8445833318e9320bf485ea125921eecc3dc97a"
PV = "xilinx+git${SRCPV}"

PACKAGE_ARCH ?= "${MACHINE_ARCH}"

COMPATIBLE_MACHINE ?= "^$"
COMPATIBLE_MACHINE_zynqmp = ".*"
COMPATIBLE_MACHINE_zynq = ".*"
COMPATIBLE_MACHINE_versal = ".*"

XSCTH_BUILD_CONFIG ?= ""

DT_FILES_PATH = "${XSCTH_WS}/${XSCTH_PROJ}"
DT_INCLUDE_append = " ${WORKDIR}"
DT_PADDING_SIZE = "0x1000"

UBOOT_DTS ?= ""
XSCTH_MISC = " -hdf_type ${HDF_EXT}"
XSCTH_APP = "device-tree"
YAML_DT_BOARD_FLAGS_zynqmp-generic ?= ""
YAML_DT_BOARD_FLAGS_versal-generic ?= ""
YAML_DT_BOARD_FLAGS_zynq-generic ?= ""
UBOOT_DTS_NAME = "u-boot"
DTB_FILE = "u-boot.dtb"

do_configure[dirs] += "${DT_FILES_PATH}"
SRC_URI_append = "${@" ".join(["file://%s" % f for f in (d.getVar('UBOOT_DTS') or "").split()])}"

do_configure_prepend () {
    if [ ! -z "${UBOOT_DTS}" ]; then
        for f in ${UBOOT_DTS}; do
            cp -rf ${WORKDIR}/${f} ${DT_FILES_PATH}/
        done
        return
    fi
}


#Both linux dtb and uboot dtb are installing
#system-top.dtb for uboot env recipe while do_prepare_recipe_sysroot
#moving system-top.dts to othername.
do_compile_prepend() {
    import shutil
   
    listpath = d.getVar("DT_FILES_PATH")
    try:
        os.remove(os.path.join(listpath, "system.dts"))
        shutil.move(os.path.join(listpath, "system-top.dts"), os.path.join(listpath, d.getVar("UBOOT_DTS_NAME") + ".dts"))
    except OSError:
        pass
}

do_deploy() {
        install -Dm 0644 ${B}/${UBOOT_DTS_NAME}.dtb ${DEPLOYDIR}/${UBOOT_DTS_NAME}.dtb
}
