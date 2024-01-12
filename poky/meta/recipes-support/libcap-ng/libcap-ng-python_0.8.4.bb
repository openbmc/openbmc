require libcap-ng.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/libcap-ng:"

SUMMARY .= " - python"

inherit lib_package autotools python3targetconfig

# drop setuptools when version > 0.8.3 is released; it's needed only for distutils
DEPENDS += "libcap-ng python3 swig-native python3-setuptools-native"

S = "${WORKDIR}/libcap-ng-${PV}"

EXTRA_OECONF += "--with-python3"

do_install:append() {
    rm -rf ${D}${bindir}
    rm -rf ${D}${libdir}/.debug
    rm -f ${D}${libdir}/lib*
    rm -rf ${D}${libdir}/pkgconfig
    rm -rf ${D}${datadir}
    rm -rf ${D}${includedir}
}

# PACKAGES = "${PN}"

FILES:${PN} = "${libdir}/python${PYTHON_BASEVERSION}"
FILES:${PN}-dbg =+ "${PYTHON_SITEPACKAGES_DIR}/.debug/_capng.so"

