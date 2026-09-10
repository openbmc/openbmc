DESCRIPTION = "Python3 interface to the wxWidgets Cross-platform C++ GUI toolkit."
HOMEPAGE = "https://www.wxpython.org"

LICENSE = "BSD-2-Clause AND LGPL-2.0-only AND LGPL-2.0-or-later WITH WxWindows-exception-3.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=102f37a0d23aa258e59e4cc8b5380b35"

DEPENDS = "python3-attrdict3-native python3-six-native wxwidgets-native \
           python3-requests-native wxwidgets \
           "

PYPI_PACKAGE = "wxpython"

SRC_URI += "file://add-back-option-build-base.patch \
           file://wxgtk-fixup-build-scripts.patch \
           file://not-overwrite-cflags-cxxflags.patch \
           file://0001-sip-Conditionally-use-GetAssertStackTrace-under-USE_.patch \
           "
SRC_URI[sha256sum] = "44e836d1bccd99c38790bb034b6ecf70d9060f6734320560f7c4b0d006144793"

inherit pypi setuptools3 cython pkgconfig features_check

# wxPython declares setuptools.build_meta in pyproject.toml, but it cannot be
# built through pyproject-build: the sdist ships a build.py in its top level
# directory, which shadows the "build" module pyproject-build itself imports:
#   from build.__main__ import entrypoint
#   ModuleNotFoundError: No module named 'build.__main__'; 'build' is not a package
# Keep driving setup.py directly until upstream stops colliding with that name.
INSANE_SKIP += "pep517-backend"

REQUIRED_DISTRO_FEATURES = "x11"

# --baselib came from an out-of-tree wx-config patch that wxwidgets dropped in
# 3.2.11; wx-config now rejects it. It is not needed either, as the wxwidgets
# recipe rewrites libdir in wx-config to an absolute ${STAGING_LIBDIR}.
export WX_CONFIG = "'${RECIPE_SYSROOT_NATIVE}${bindir}/wx-config --prefix=${STAGING_EXECPREFIXDIR}'"

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
