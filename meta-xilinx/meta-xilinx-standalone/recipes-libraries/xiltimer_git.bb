inherit esw python3native

ESW_COMPONENT_SRC = "/lib/sw_services/xiltimer/src/"
ESW_COMPONENT_NAME = "libxiltimer.a"

DEPENDS += "dtc-native python3-dtc-native python3-pyyaml-native libxil device-tree"

do_configure_prepend() {
    # This script should also not rely on relative paths and such
    cd ${S}
    nativepython3 ${S}/scripts/lib_parser.py -d ${DTBFILE} -o ${OECMAKE_SOURCEPATH}
}
