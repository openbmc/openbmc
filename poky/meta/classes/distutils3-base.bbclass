DEPENDS:append:class-target = " ${PYTHON_PN}-native ${PYTHON_PN}"
DEPENDS:append:class-nativesdk = " ${PYTHON_PN}-native ${PYTHON_PN}"
RDEPENDS:${PN} += "${@['', '${PYTHON_PN}-core']['${CLASSOVERRIDE}' == 'class-target']}"

inherit distutils-common-base python3native python3targetconfig

