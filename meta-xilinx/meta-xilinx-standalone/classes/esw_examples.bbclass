inherit esw deploy python3native

DEPENDS += "dtc-native python3-dtc-native python3-pyyaml-native xilstandalone libxil xiltimer device-tree"

do_configure_prepend() {
    cd ${S}
    nativepython3 ${S}/scripts/linker_gen.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH}
}

do_generate_eglist () {
    cd ${S}
    nativepython3 ${S}/scripts/example.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH}
}
addtask generate_eglist before do_configure after do_prepare_recipe_sysroot
do_prepare_recipe_sysroot[rdeptask] = "do_unpack"

do_install() {
    install -d ${D}/${base_libdir}/firmware
    install -m 0755  ${B}/*.elf ${D}/${base_libdir}/firmware
}

do_deploy() {
    install -Dm 0644 ${WORKDIR}/package/${base_libdir}/firmware/*.elf ${DEPLOYDIR}/
}
addtask deploy before do_build after do_package

FILES_${PN} = "${base_libdir}/firmware/*.elf"
