inherit esw deploy

COMPATIBLE_MACHINE = ".*-zynqmp"

ESW_COMPONENT_SRC = "/lib/sw_apps/zynqmp_fsbl/src"

DEPENDS += "xilstandalone xiltimer xilffs xilsecure xilpm device-tree"

PSU_INIT = "${RECIPE_SYSROOT}/${includedir}/devicetree/psu_init*"

do_configure_prepend() {
    # Copy psu_init* files to fsbl source code
    cp ${PSU_INIT} ${S}/${ESW_COMPONENT_SRC}
}

do_install() {
    install -d ${D}/${base_libdir}/firmware
    # Note that we have to make the ELF executable for it to be stripped
    install -m 0755  ${B}/zynqmp_fsbl* ${D}/${base_libdir}/firmware
}

ZYNQMP_FSBL_BASE_NAME ?= "${BPN}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
ZYNQMP_FSBL_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy() {

    # We need to deploy the stripped elf, hence why not doing it from ${D}
    install -Dm 0644 ${WORKDIR}/package/${base_libdir}/firmware/zynqmp_fsbl.elf ${DEPLOYDIR}/${ZYNQMP_FSBL_BASE_NAME}.elf
    ln -sf ${ZYNQMP_FSBL_BASE_NAME}.elf ${DEPLOYDIR}/${BPN}-${MACHINE}.elf
    ${OBJCOPY} -O binary ${WORKDIR}/package/${base_libdir}/firmware/zynqmp_fsbl.elf ${WORKDIR}/package/${base_libdir}/firmware/zynqmp_fsbl.bin
    install -m 0644 ${WORKDIR}/package/${base_libdir}/firmware/zynqmp_fsbl.bin ${DEPLOYDIR}/${ZYNQMP_FSBL_BASE_NAME}.bin
    ln -sf ${ZYNQMP_FSBL_BASE_NAME}.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.bin
}

addtask deploy before do_build after do_package

CFLAGS_append_aarch64 = " -DARMA53_64"
CFLAGS_append_armrm = " -DARMR5"

FILES_${PN} = "${base_libdir}/firmware/zynqmp_fsbl*"
