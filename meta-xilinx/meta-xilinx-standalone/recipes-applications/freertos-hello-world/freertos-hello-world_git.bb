inherit esw deploy

ESW_COMPONENT_SRC = "/lib/sw_apps/freertos_hello_world/src/"

DEPENDS += "dtc-native python3-dtc-native libxil xilstandalone xiltimer freertos10-xilinx device-tree"

do_configure_prepend() {
    cd ${S}
    nativepython3 ${S}/scripts/linker_gen.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH}
}

do_install() {
    install -d ${D}/${base_libdir}/firmware
    # Note that we have to make the ELF executable for it to be stripped
    install -m 0755  ${B}/freertos_hello_world* ${D}/${base_libdir}/firmware
}

FREERTOS_HELLO_WORLD_BASE_NAME ?= "${BPN}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
FREERTOS_HELLO_WORLD_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy() {

    # We need to deploy the stripped elf, hence why not doing it from ${D}
    install -Dm 0644 ${WORKDIR}/package/${base_libdir}/firmware/freertos_hello_world.elf ${DEPLOYDIR}/${FREERTOS_HELLO_WORLD_BASE_NAME}.elf
    ln -sf ${FREERTOS_HELLO_WORLD_BASE_NAME}.elf ${DEPLOYDIR}/${BPN}-${MACHINE}.elf 
    ${OBJCOPY} -O binary ${WORKDIR}/package/${base_libdir}/firmware/freertos_hello_world.elf ${WORKDIR}/package/${base_libdir}/firmware/freertos_hello_world.bin
    install -m 0644 ${WORKDIR}/package/${base_libdir}/firmware/freertos_hello_world.bin ${DEPLOYDIR}/${FREERTOS_HELLO_WORLD_BASE_NAME}.bin
    ln -sf ${FREERTOS_HELLO_WORLD_BASE_NAME}.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.bin
}

addtask deploy before do_build after do_package

FILES_${PN} = "${base_libdir}/firmware/freertos_hello_world*"
