inherit esw python3native

ESW_COMPONENT_SRC = "/ThirdParty/sw_services/lwip211/src/"
ESW_COMPONENT_NAME = "liblwip211.a"

DEPENDS += "dtc-native python3-dtc-native libxil python3-pyyaml-native"
DEPENDS_append_xilinx-freertos = "freertos10-xilinx"

do_configure_prepend() {
    # This script should also not rely on relative paths and such
    cd ${S}
    nativepython3 ${S}/scripts/lib_parser.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH}
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -m 0755  ${B}/${ESW_COMPONENT_NAME} ${D}${libdir}
    install -m 0644  ${B}/include/*.h ${D}${includedir}
    cp -r ${B}/include/arch/ ${D}${includedir}
    cp -r ${B}/include/include/lwip/ ${D}${includedir}
    cp -r ${B}/include/include/netif/ ${D}${includedir}
}
