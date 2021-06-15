inherit esw python3native

ESW_COMPONENT_SRC = "/lib/bsp/standalone/src/"
ESW_COMPONENT_NAME = "libxilstandalone.a"

DEPENDS += "dtc-native python3-dtc-native python3-pyyaml-native libgloss device-tree"

do_configure_prepend() {
    # This script should also not rely on relative paths and such
    cd ${S}
    nativepython3 ${S}/scripts/generate_libdata.py -d ${DTBFILE}
}
