SUMMARY = "Python interface to PROJ (cartographic projections and coordinate transformations library)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=873757af01d2d221eedb422c4c1dd163"
DEPENDS = "python3-cython proj"
DEPENDS:append:class-target = " python3-cython-native proj-native"

PYPI_PACKAGE = "pyproj"

inherit pypi setuptools3

SRC_URI[sha256sum] = "44aa7c704c2b7d8fb3d483bbf75af6cb2350d30a63b144279a09b75fead501bf"

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
