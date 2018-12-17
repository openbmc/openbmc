SUMMARY = "SIP is a C++/Python Wrapper Generator"
HOMEPAGE = "http://www.riverbankcomputing.co.uk/sip"
SECTION = "devel"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2;md5=e91355d8a6f8bd8f7c699d62863c7303"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/pyqt/sip/sip-${PV}/sip-${PV}.tar.gz \
          "
SRC_URI[md5sum] = "9124cb8978742685747a5415179a9890"                           
SRC_URI[sha256sum] = "e353a7056599bf5fbd5d3ff9842a6ab2ea3cf4e0304a0f925ec5862907c0d15e"

S = "${WORKDIR}/sip-${PV}"

BBCLASSEXTEND = "native"

do_configure_prepend_class-target() {
    echo "py_platform = linux" > sip.cfg
    echo "py_inc_dir = %(sysroot)/${includedir}/python${PYTHON_BASEVERSION}${PYTHON_ABI}" >> sip.cfg
    echo "sip_bin_dir = ${D}/${bindir}" >> sip.cfg
    echo "sip_inc_dir = ${D}/${includedir}" >> sip.cfg
    echo "sip_module_dir = ${D}/${libdir}/python${PYTHON_BASEVERSION}${PYTHON_ABI}/site-packages" >> sip.cfg
    echo "sip_sip_dir = ${D}/${datadir}/sip" >> sip.cfg
    ${PYTHON} configure.py --configuration sip.cfg --sip-module PyQt5.sip --sysroot ${STAGING_DIR_HOST} CC="${CC}" CXX="${CXX}" LINK="${CXX}" STRIP="" LINK_SHLIB="${CXX}" CFLAGS="${CFLAGS}" CXXFLAGS="${CXXFLAGS}" LFLAGS="${LDFLAGS}"
}
do_configure_prepend_class-native() {
    echo "py_platform = linux" > sip.cfg
    echo "py_inc_dir = ${includedir}/python${PYTHON_BASEVERSION}${PYTHON_ABI}" >> sip.cfg
    echo "sip_bin_dir = ${D}/${bindir}" >> sip.cfg
    echo "sip_inc_dir = ${D}/${includedir}" >> sip.cfg
    echo "sip_module_dir = ${D}/${libdir}/python${PYTHON_BASEVERSION}${PYTHON_ABI}/site-packages" >> sip.cfg
    echo "sip_sip_dir = ${D}/${datadir}/sip" >> sip.cfg
    ${PYTHON} configure.py --configuration sip.cfg --sip-module PyQt5.sip --sysroot=${STAGING_DIR_NATIVE}
}
do_install() {
    oe_runmake install
}
