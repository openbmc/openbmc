SUMMARY = "Python bindings for the Apache Thrift RPC system"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=26a2009ddcb7c4162f8bafd5ef73c670"

SRC_URI[md5sum] = "76bb768a210257dd203923110042e560"
SRC_URI[sha256sum] = "b7f6c09155321169af03f9fb20dc15a4a0c7481e7c334a5ba8f7f0d864633209"

PYPI_PACKAGE_EXT = "zip"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"
