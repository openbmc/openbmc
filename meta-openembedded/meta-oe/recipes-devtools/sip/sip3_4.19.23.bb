SUMMARY = "SIP is a C++/Python Wrapper Generator"
HOMEPAGE = "https://riverbankcomputing.com/software/sip/"
SECTION = "devel"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2;md5=e91355d8a6f8bd8f7c699d62863c7303"

SRC_URI = "https://www.riverbankcomputing.com/static/Downloads/sip/${PV}/sip-${PV}.tar.gz \
"
SRC_URI[md5sum] = "70adc0c9734e2d9dcd241d3f931dfc74"
SRC_URI[sha256sum] = "22ca9bcec5388114e40d4aafd7ccd0c4fe072297b628d0c5cdfa2f010c0bc7e7"

inherit python3-dir python3native

S = "${WORKDIR}/sip-${PV}"

DEPENDS = "python3"

PACKAGES += "python3-sip3"

BBCLASSEXTEND = "native"

do_configure_prepend_class-target() {
    echo "py_platform = linux" > sip.cfg
    echo "py_inc_dir = %(sysroot)/${includedir}/python%(py_major).%(py_minor)${PYTHON_ABI}" >> sip.cfg
    echo "sip_bin_dir = ${D}/${bindir}" >> sip.cfg
    echo "sip_inc_dir = ${D}/${includedir}" >> sip.cfg
    echo "sip_module_dir = ${D}/${libdir}/python%(py_major).%(py_minor)/site-packages" >> sip.cfg
    echo "sip_sip_dir = ${D}/${datadir}/sip" >> sip.cfg
    ${PYTHON} configure.py --configuration sip.cfg --sip-module PyQt5.sip --sysroot ${STAGING_DIR_HOST} CC="${CC}" CXX="${CXX}" LINK="${CXX}" STRIP="" LINK_SHLIB="${CXX}" CFLAGS="${CFLAGS}" CXXFLAGS="${CXXFLAGS}" LFLAGS="${LDFLAGS}"
}

do_configure_prepend_class-native() {
    echo "py_platform = linux" > sip.cfg
    echo "py_inc_dir = ${includedir}/python%(py_major).%(py_minor)${PYTHON_ABI}" >> sip.cfg
    echo "sip_bin_dir = ${D}/${bindir}" >> sip.cfg
    echo "sip_inc_dir = ${D}/${includedir}" >> sip.cfg
    echo "sip_module_dir = ${D}/${libdir}/python%(py_major).%(py_minor)/site-packages" >> sip.cfg
    echo "sip_sip_dir = ${D}/${datadir}/sip" >> sip.cfg
    ${PYTHON} configure.py --configuration sip.cfg --sip-module PyQt5.sip --sysroot=${STAGING_DIR_NATIVE}
}

do_install() {
    oe_runmake install
}

FILES_python3-sip3 = "${libdir}/${PYTHON_DIR}/site-packages/"
FILES_${PN}-dbg += "${libdir}/${PYTHON_DIR}/site-packages/.debug"
