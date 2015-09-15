inherit distutils-common-base

DEPENDS  += "${@["${PYTHON_PN}-native", ""][(d.getVar('PACKAGES', True) == '')]}"
