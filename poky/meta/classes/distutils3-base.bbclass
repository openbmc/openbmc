DEPENDS  += "${@["${PYTHON_PN}-native ${PYTHON_PN}", ""][(d.getVar('PACKAGES') == '')]}"
RDEPENDS_${PN} += "${@['', '${PYTHON_PN}-core']['${CLASSOVERRIDE}' == 'class-target']}"

inherit distutils-common-base python3native python3targetconfig

