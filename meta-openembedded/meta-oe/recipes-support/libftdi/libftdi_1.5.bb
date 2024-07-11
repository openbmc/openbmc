DESCRIPTION = "libftdi is a library to talk to FTDI chips.\
FT232BM/245BM, FT2232C/D and FT232/245R using libusb,\
including the popular bitbang mode."
HOMEPAGE = "http://www.intra2net.com/en/developer/libftdi/"
SECTION = "libs"
LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM= "\
    file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
"

DEPENDS = "libusb1 python3 swig-native"

SRC_URI = "http://www.intra2net.com/en/developer/${BPN}/download/${BPN}1-${PV}.tar.bz2"
SRC_URI[sha256sum] = "7c7091e9c86196148bd41177b4590dccb1510bfe6cea5bf7407ff194482eb049"

S = "${WORKDIR}/${BPN}1-${PV}"

inherit cmake binconfig pkgconfig python3native

PACKAGECONFIG ??= ""
PACKAGECONFIG[cpp-wrapper] = "-DFTDI_BUILD_CPP=on -DFTDIPP=on,-DFTDI_BUILD_CPP=off -DFTDIPP=off,boost"
PACKAGECONFIG[ftdi-eeprom] = "-DFTDI_EEPROM=on,-DFTDI_EEPROM=off,libconfuse"

EXTRA_OECMAKE = "-DSTATICLIBS=off -DEXAMPLES=off \
                 -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
                 -DPYTHON_LIBRARY=${STAGING_LIBDIR}/lib${PYTHON_DIR}${PYTHON_ABI}.so \
                 -DPYTHON_INCLUDE_DIR=${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI}"

do_install:append() {
    # remove absolute paths
    sed -i -e 's|${RECIPE_SYSROOT}||g' ${D}${libdir}/cmake/libftdi1/LibFTDI1Config.cmake
}

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "${PN}-python ftdi-eeprom"

FILES:ftdi-eeprom = "${bindir}/ftdi_eeprom"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/"
