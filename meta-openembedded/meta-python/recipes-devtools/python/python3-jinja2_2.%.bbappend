# Main recipe was moved to oe-core, but with ptest disabled
inherit ${@bb.utils.filter('DISTRO_FEATURES', 'ptest', d)}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-toml \
    ${PYTHON_PN}-unixadmin \
"
