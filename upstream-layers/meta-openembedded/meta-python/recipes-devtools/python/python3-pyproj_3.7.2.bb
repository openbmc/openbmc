SUMMARY = "Python interface to PROJ (cartographic projections and coordinate transformations library)"
LICENSE = "MIT"
HOMEPAGE = "https://pyproj4.github.io/pyproj/stable/index.html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f3574263859ef2dc9bd7817d51adbaa"

inherit pypi python_setuptools_build_meta cython

SRC_URI += "file://rpath.patch"

SRC_URI[sha256sum] = "39a0cf1ecc7e282d1d30f36594ebd55c9fae1fda8a2622cee5d100430628f88c"

DEPENDS = "proj proj-native"

RDEPENDS:${PN} = " \
    python3-certifi \
    python3-compression \
    python3-json \
    python3-logging \
    python3-profile \
"

export PROJ_INCDIR = "${STAGING_INCDIR}"
export PROJ_LIBDIR = "${STAGING_LIBDIR}"
export PROJ_DIR = "${STAGING_BINDIR_NATIVE}/.."

BBCLASSEXTEND = "native"
