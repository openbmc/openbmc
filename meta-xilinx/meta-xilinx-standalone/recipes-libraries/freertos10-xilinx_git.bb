inherit esw python3native

ESW_COMPONENT_SRC = "/ThirdParty/bsp/freertos10_xilinx/src/"
ESW_COMPONENT_NAME = "libfreertos.a"

DEPENDS += "libxil xilstandalone xilmem dtc-native python3-pyyaml-native python3-dtc-native xiltimer"

do_configure_prepend() {
    # This script should also not rely on relative paths and such
    cd ${S}
    nativepython3 ${S}/scripts/lib_parser.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH} 
}
