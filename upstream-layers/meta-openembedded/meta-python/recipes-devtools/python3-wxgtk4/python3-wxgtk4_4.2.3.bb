DESCRIPTION = "Python3 interface to the wxWidgets Cross-platform C++ GUI toolkit."
HOMEPAGE = "https://www.wxpython.org"

LICENSE = "LGPL-2.0-only & WXwindows & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=102f37a0d23aa258e59e4cc8b5380b35"

DEPENDS = "python3-attrdict3-native python3-six-native wxwidgets-native \
           python3-requests-native wxwidgets \
           "

PYPI_PACKAGE = "wxPython"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI += "file://add-back-option-build-base.patch \
           file://wxgtk-fixup-build-scripts.patch \
           file://not-overwrite-cflags-cxxflags.patch \
           file://0001-sip-Conditionally-use-GetAssertStackTrace-under-USE_.patch \
           "
SRC_URI[sha256sum] = "20d6e0c927e27ced85643719bd63e9f7fd501df6e9a8aab1489b039897fd7c01"

S = "${UNPACKDIR}/wxPython-${PV}"

inherit pypi setuptools3 pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

export WX_CONFIG = "'${RECIPE_SYSROOT_NATIVE}${bindir}/wx-config --prefix=${STAGING_EXECPREFIXDIR} --baselib=${baselib}'"

RDEPENDS:${PN} = "\
    python3-difflib \
    python3-image \
    python3-numpy \
    python3-pillow \
    python3-pip \
    python3-pprint \
    python3-pycairo \
    python3-six \
    python3-xml \
    python3-cairocffi \
" 
