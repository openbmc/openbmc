SUMMARY = "Python interface to PROJ (cartographic projections and coordinate transformations library)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc4bb2668871655e24030dfe8d2a7ce7"
DEPENDS = "python3-cython proj"
DEPENDS:append:class-target = " python3-cython-native proj-native"

PYPI_PACKAGE = "pyproj"

inherit pypi setuptools3

SRC_URI[sha256sum] = "261eb29b1d55b1eb7f336127344d9b31284d950a9446d1e0d1c2411f7dd8e3ac"

RDEPENDS:${PN} = "${PYTHON_PN}-certifi proj"

export PROJ_INCDIR = "${STAGING_INCDIR}"
export PROJ_LIBDIR = "${STAGING_LIBDIR}"
export PROJ_DIR = "${STAGING_BINDIR_NATIVE}/.."
