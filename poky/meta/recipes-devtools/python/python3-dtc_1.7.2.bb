SUMMARY = "Python Library for the Device Tree Compiler"
HOMEPAGE = "https://devicetree.org/"
DESCRIPTION = "A python library for the Device Tree Compiler, a tool used to manipulate Device Tree files which contain a data structure for describing hardware."
SECTION = "bootloader"
LICENSE = "GPL-2.0-only | BSD-2-Clause"

DEPENDS = "flex-native bison-native swig-native python3-setuptools-scm-native libyaml dtc"

SRC_URI = "git://git.kernel.org/pub/scm/utils/dtc/dtc.git;branch=main \
           "

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

LIC_FILES_CHKSUM = "file://pylibfdt/libfdt.i;beginline=1;endline=6;md5=afda088c974174a29108c8d80b5dce90"

SRCREV = "2d10aa2afe35527728db30b35ec491ecb6959e5c"

S = "${WORKDIR}/git"

PYPA_WHEEL = "${S}/dist/libfdt-1.6.2*.whl"

inherit setuptools3 pkgconfig

BBCLASSEXTEND = "native nativesdk"
