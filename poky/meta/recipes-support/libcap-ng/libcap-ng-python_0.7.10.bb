require libcap-ng.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/libcap-ng:"

SUMMARY .= " - python"

inherit lib_package autotools python3native

DEPENDS += "libcap-ng python3 swig-native"

S = "${WORKDIR}/libcap-ng-${PV}"

EXTRA_OECONF += "--with-python --with-python3"
EXTRA_OEMAKE += "PYLIBVER='python${PYTHON_BASEVERSION}${PYTHON_ABI}' PYINC='${STAGING_INCDIR}/${PYLIBVER}'"

do_install_append() {
    rm -rf ${D}${bindir}
    rm -rf ${D}${libdir}/.debug
    rm -f ${D}${libdir}/lib*
    rm -rf ${D}${libdir}/pkgconfig
    rm -rf ${D}${datadir}
    rm -rf ${D}${includedir}
}

# PACKAGES = "${PN}"

FILES_${PN} = "${libdir}/python${PYTHON_BASEVERSION}"
FILES_${PN}-dbg =+ "${PYTHON_SITEPACKAGES_DIR}/.debug/_capng.so"

