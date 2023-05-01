SUMMARY = "Python interface to PROJ (cartographic projections and coordinate transformations library)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=873757af01d2d221eedb422c4c1dd163"
DEPENDS = "python3-cython proj"
DEPENDS:append:class-target = " python3-cython-native proj-native"

PYPI_PACKAGE = "pyproj"

inherit pypi setuptools3

SRC_URI[sha256sum] = "9859d1591c1863414d875ae0759e72c2cffc01ab989dc64137fbac572cc81bf6"

RDEPENDS:${PN} = "${PYTHON_PN}-certifi proj"

export PROJ_INCDIR = "${STAGING_INCDIR}"
export PROJ_LIBDIR = "${STAGING_LIBDIR}"
export PROJ_DIR = "${STAGING_BINDIR_NATIVE}/.."
