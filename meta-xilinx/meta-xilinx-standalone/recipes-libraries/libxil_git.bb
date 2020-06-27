inherit esw python3native

ESW_COMPONENT_SRC = "/XilinxProcessorIPLib/drivers/"
ESW_COMPONENT_NAME = "libxil.a"

DEPENDS += "dtc-native python3-dtc-native python3-pyyaml-native xilstandalone xilmem device-tree"

do_configure_prepend() {
    # This will generate CMakeLists.txt which contains
    # drivers for the libxil 
    cd ${S}
    #TODO
    # This call was initially used to get the list of drivers and libraries required
    # by the design to the build system to use as dependencies to the application
    # being built, at this point this is all done in a single cmake build bundling
    # everything in libxil, which is undesired.
    DRIVERS_LIST=$(nativepython3 ${S}/scripts/getdrvlist.py -d ${DTBFILE})
}

do_generate_driver_data() {
    # This script should also not rely on relative paths and such
    cd ${S}
    nativepython3 ${S}/scripts/generate_drvdata.py -d ${DTBFILE}
}

addtask do_generate_driver_data before do_configure after do_prepare_recipe_sysroot
do_prepare_recipe_sysroot[rdeptask] = "do_unpack"
