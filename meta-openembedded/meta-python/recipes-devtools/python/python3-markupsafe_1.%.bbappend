# Main recipe was moved to oe-core, but with ptest disabled
inherit ${@bb.utils.filter('DISTRO_FEATURES', 'ptest', d)}

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -f ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

