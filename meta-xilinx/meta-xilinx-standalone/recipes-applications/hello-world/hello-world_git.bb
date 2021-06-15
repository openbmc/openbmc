inherit esw deploy

ESW_COMPONENT_SRC = "/lib/sw_apps/hello_world/src/"

DEPENDS += "dtc-native python3-dtc-native libxil xiltimer device-tree"

inherit python3native

do_configure_prepend() {
    cd ${S}
    nativepython3 ${S}/scripts/linker_gen.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH}
}

do_install() {
    install -d ${D}/${base_libdir}/firmware
    # Note that we have to make the ELF executable for it to be stripped
    install -m 0755  ${B}/hello_world* ${D}/${base_libdir}/firmware
}

HELLO_WORLD_BASE_NAME ?= "${BPN}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
HELLO_WORLD_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy() {

    # We need to deploy the stripped elf, hence why not doing it from ${D}
    install -Dm 0644 ${WORKDIR}/package/${base_libdir}/firmware/hello_world.elf ${DEPLOYDIR}/${HELLO_WORLD_BASE_NAME}.elf
    ln -sf ${HELLO_WORLD_BASE_NAME}.elf ${DEPLOYDIR}/${BPN}-${MACHINE}.elf
    ${OBJCOPY} -O binary ${WORKDIR}/package/${base_libdir}/firmware/hello_world.elf ${WORKDIR}/package/${base_libdir}/firmware/hello_world.bin
    install -m 0644 ${WORKDIR}/package/${base_libdir}/firmware/hello_world.bin ${DEPLOYDIR}/${HELLO_WORLD_BASE_NAME}.bin
    ln -sf ${HELLO_WORLD_BASE_NAME}.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.bin
}

addtask deploy before do_build after do_package

FILES_${PN} = "${base_libdir}/firmware/hello_world*"
