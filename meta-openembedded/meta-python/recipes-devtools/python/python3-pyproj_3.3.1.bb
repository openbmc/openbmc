SUMMARY = "Python interface to PROJ (cartographic projections and coordinate transformations library)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc4bb2668871655e24030dfe8d2a7ce7"
DEPENDS = "python3-cython proj"
DEPENDS:append:class-target = " python3-cython-native proj-native"

PYPI_PACKAGE = "pyproj"

inherit pypi setuptools3

SRC_URI[sha256sum] = "b3d8e14d91cc95fb3dbc03a9d0588ac58326803eefa5bbb0978d109de3304fbe"

RDEPENDS:${PN} = "${PYTHON_PN}-certifi proj"

export PROJ_INCDIR = "${STAGING_INCDIR}"
export PROJ_LIBDIR = "${STAGING_LIBDIR}"
