require python3-scons_${PV}.bb
inherit native python3native
DEPENDS = "python3-native python3-setuptools-native"

do_install_append() {
    create_wrapper ${D}${bindir}/scons SCONS_LIB_DIR='${STAGING_DIR_HOST}/${PYTHON_SITEPACKAGES_DIR}' PYTHONNOUSERSITE='1'
}
