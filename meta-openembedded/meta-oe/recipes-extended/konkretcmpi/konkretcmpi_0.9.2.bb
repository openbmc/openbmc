SUMMARY = "Tool for rapid CMPI providers development"
DESCRIPTION = "\
KonkretCMPI makes CMPI provider development easier by generating type-safe \
concrete CIM interfaces from MOF definitions and by providing default \
implementations for many of the provider operations."
HOMEPAGE = "https://github.com/rnovacek/konkretcmpi"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f673270bfc350d9ce1efc8724c6c1873"
DEPENDS_append_class-target = " swig-native sblim-cmpi-devel python"
DEPENDS_append_class-native = " cmpi-bindings-native"

SRC_URI = "git://github.com/rnovacek/konkretcmpi.git \
           file://0001-CMakeLists.txt-fix-lib64-can-not-be-shiped-in-64bit-.patch \
           file://0001-drop-including-rpath-cmake-module.patch \
           "

SRCREV = "ad28225e6eceff88417a60c1ba8896c8e40f21a7"
S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DWITH_PYTHON=ON \
                 ${@oe.utils.conditional("libdir", "/usr/lib64", "-DLIB_SUFFIX=64", "", d)} \
                 ${@oe.utils.conditional("libdir", "/usr/lib32", "-DLIB_SUFFIX=32", "", d)} \
                "

LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"

do_install_append() {
    rm -rf ${D}${datadir}
}

PACKAGES =+ "${PN}-python"

RPROVIDES_${PN}-dbg += "${PN}-python-dbg"

FILES_${PN}-python = "${libdir}/python2.7/site-packages/konkretmof.py* ${libdir}/python2.7/site-packages/_konkretmof.so"

BBCLASSEXTEND = "native"
