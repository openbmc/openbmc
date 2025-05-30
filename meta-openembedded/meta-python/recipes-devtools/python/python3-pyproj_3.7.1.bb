SUMMARY = "Python interface to PROJ (cartographic projections and coordinate transformations library)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f3574263859ef2dc9bd7817d51adbaa"

inherit pypi python_setuptools_build_meta cython

SRC_URI += "file://rpath.patch"

SRC_URI[sha256sum] = "60d72facd7b6b79853f19744779abcd3f804c4e0d4fa8815469db20c9f640a47"

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
