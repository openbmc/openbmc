DESCRIPTION = "Python3 interface to the wxWidgets Cross-platform C++ GUI toolkit."
HOMEPAGE = "http://www.wxpython.org"

LICENSE = "WXwindows"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=fce1d18e2d633d41786c0a8dfbc80917"

inherit features_check

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'no_gui', '', 'x11', d)}"

DEPENDS = "wxwidgets-native wxwidgets"

PYPI_PACKAGE = "wxPython"

SRC_URI += "file://add-back-option-build-base.patch \
            file://wxgtk-fixup-build-scripts.patch \
            file://sip-fix-override-functions.patch \
            "
SRC_URI[sha256sum] = "00e5e3180ac7f2852f342ad341d57c44e7e4326de0b550b9a5c4a8361b6c3528"

S = "${WORKDIR}/wxPython-${PV}"

inherit pypi setuptools3 pkgconfig

export WX_CONFIG = "'${RECIPE_SYSROOT_NATIVE}${bindir}/wx-config --prefix=${STAGING_EXECPREFIXDIR}'"

RDEPENDS:${PN} = "\
    python3-difflib \
    python3-image \
    python3-numpy \
    python3-pillow \
    python3-pprint \
    python3-pycairo \
    python3-six \
    python3-xml \
"
