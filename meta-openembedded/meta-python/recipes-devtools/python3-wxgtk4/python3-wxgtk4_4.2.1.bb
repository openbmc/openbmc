DESCRIPTION = "Python3 interface to the wxWidgets Cross-platform C++ GUI toolkit."
HOMEPAGE = "http://www.wxpython.org"

LICENSE = "LGPL-2.0-only & WXwindows"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=bdabf9e11191e2b9d3b6aef5f338ec00"

DEPENDS = "python3-attrdict3-native python3-six-native wxwidgets-native \
           wxwidgets \
           "

PYPI_PACKAGE = "wxPython"

SRC_URI += "file://add-back-option-build-base.patch \
           file://wxgtk-fixup-build-scripts.patch \
           file://not-overwrite-cflags-cxxflags.patch \
           file://0001-sip-Conditionally-use-GetAssertStackTrace-under-USE_.patch \
           "
SRC_URI[sha256sum] = "e48de211a6606bf072ec3fa778771d6b746c00b7f4b970eb58728ddf56d13d5c"

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
