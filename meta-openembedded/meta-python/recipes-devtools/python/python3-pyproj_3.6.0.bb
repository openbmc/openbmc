SUMMARY = "Python interface to PROJ (cartographic projections and coordinate transformations library)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=873757af01d2d221eedb422c4c1dd163"
DEPENDS = "python3-cython proj"
DEPENDS:append:class-target = " python3-cython-native proj-native"

PYPI_PACKAGE = "pyproj"

inherit pypi setuptools3

SRC_URI[sha256sum] = "a5b111865b3f0f8b77b3983f2fbe4dd6248fc09d3730295949977c8dcd988062"

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
