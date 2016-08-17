SUMMARY = "Tool for rapid CMPI providers development"
DESCRIPTION = "\
KonkretCMPI makes CMPI provider development easier by generating type-safe \
concrete CIM interfaces from MOF definitions and by providing default \
implementations for many of the provider operations."
HOMEPAGE = "https://github.com/rnovacek/konkretcmpi"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f673270bfc350d9ce1efc8724c6c1873"
DEPENDS = "swig-native sblim-cmpi-devel python"

SRC_URI = "git://github.com/rnovacek/konkretcmpi.git \
           file://konkretcmpi-0.9.2-fix-returning-instance-from-method.patch \
           file://0001-CMakeLists.txt-fix-lib64-can-not-be-shiped-in-64bit-.patch \
           "

SRCREV = "460e6421c16a8216d29ccd1b7490f814dab8b769"
S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DWITH_PYTHON=ON \
                 ${@base_conditional("libdir", "/usr/lib64", "-DLIB_SUFFIX=64", "", d)} \
                 ${@base_conditional("libdir", "/usr/lib32", "-DLIB_SUFFIX=32", "", d)} \
                "

do_install_append() {
    rm -rf ${D}${datadir}
}

PACKAGES =+ "${PN}-python ${PN}-python-dbg"

FILES_${PN}-python = "${libdir}/python2.7/site-packages/konkretmof.py* ${libdir}/python2.7/site-packages/_konkretmof.so"
FILES_${PN}-python-dbg = "${libdir}/python2.7/site-packages/.debug/*"

