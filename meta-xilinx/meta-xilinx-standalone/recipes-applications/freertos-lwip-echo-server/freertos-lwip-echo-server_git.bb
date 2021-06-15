inherit esw deploy python3native

ESW_COMPONENT_SRC = "/lib/sw_apps/freertos_lwip_echo_server/src/"

DEPENDS += "dtc-native python3-dtc-native libxil lwip xiltimer device-tree python3-pyyaml-native freertos10-xilinx"

do_configure_prepend() {
    cd ${S}
    nativepython3 ${S}/scripts/linker_gen.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH}
}

do_generate_app_data() {
    # This script should also not rely on relative paths and such
    cd ${S}
    nativepython3 ${S}/scripts/lib_parser.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH}
}
addtask do_generate_app_data before do_configure after do_prepare_recipe_sysroot
do_prepare_recipe_sysroot[rdeptask] = "do_unpack"

do_install() {
    install -d ${D}/${base_libdir}/firmware
    # Note that we have to make the ELF executable for it to be stripped
    install -m 0755  ${B}/freertos_lwip_echo* ${D}/${base_libdir}/firmware
}

FREERTOS_LWIP_ECHO_BASE_NAME ?= "${BPN}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
FREERTOS_LWIP_ECHO_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy() {

    # We need to deploy the stripped elf, hence why not doing it from ${D}
    install -Dm 0644 ${WORKDIR}/package/${base_libdir}/firmware/freertos_lwip_echo_server.elf ${DEPLOYDIR}/${FREERTOS_LWIP_ECHO_BASE_NAME}.elf
    ln -sf ${FREERTOS_LWIP_ECHO_BASE_NAME}.elf ${DEPLOYDIR}/${BPN}-${MACHINE}.elf
    ${OBJCOPY} -O binary ${WORKDIR}/package/${base_libdir}/firmware/freertos_lwip_echo_server.elf ${WORKDIR}/package/${base_libdir}/firmware/freertos_lwip_echo_server.bin
    install -m 0644 ${WORKDIR}/package/${base_libdir}/firmware/freertos_lwip_echo_server.bin ${DEPLOYDIR}/${FREERTOS_LWIP_ECHO_BASE_NAME}.bin
    ln -sf ${FREERTOS_LWIP_ECHO_BASE_NAME}.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.bin
}

addtask deploy before do_build after do_package

FILES_${PN} = "${base_libdir}/firmware/freertos_lwip_echo*"
