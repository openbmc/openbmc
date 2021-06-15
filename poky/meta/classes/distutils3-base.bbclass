DEPENDS_append_class-target = " ${PYTHON_PN}-native ${PYTHON_PN}"
DEPENDS_append_class-nativesdk = " ${PYTHON_PN}-native ${PYTHON_PN}"
RDEPENDS_${PN} += "${@['', '${PYTHON_PN}-core']['${CLASSOVERRIDE}' == 'class-target']}"

inherit distutils-common-base python3native python3targetconfig

