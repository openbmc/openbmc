DESCRIPTION = "Python3 interface to the wxWidgets Cross-platform C++ GUI toolkit."
HOMEPAGE = "http://www.wxpython.org"

LICENSE = "LGPL-2.0-only & WXwindows & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=102f37a0d23aa258e59e4cc8b5380b35"

DEPENDS = "python3-attrdict3-native python3-six-native wxwidgets-native \
           wxwidgets \
           "

PYPI_PACKAGE = "wxPython"

SRC_URI += "file://add-back-option-build-base.patch \
           file://wxgtk-fixup-build-scripts.patch \
           file://not-overwrite-cflags-cxxflags.patch \
           file://0001-sip-Conditionally-use-GetAssertStackTrace-under-USE_.patch \
           "
SRC_URI[sha256sum] = "5dbcb0650f67fdc2c5965795a255ffaa3d7b09fb149aa8da2d0d9aa44e38e2ba"

S = "${WORKDIR}/wxPython-${PV}"

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
"
