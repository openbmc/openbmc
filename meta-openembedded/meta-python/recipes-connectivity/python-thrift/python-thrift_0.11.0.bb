SUMMARY = "Python bindings for the Apache Thrift RPC system"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=e3641ae1a26cf5c12a3a71bd3030ab0e"

SRC_URI[md5sum] = "36165d5c80e6b101dbe9fcf7ef524d51"
SRC_URI[sha256sum] = "7d59ac4fdcb2c58037ebd4a9da5f9a49e3e034bf75b3f26d9fe48ba3d8806e6b"

inherit pypi setuptools

# Use different filename to prevent conflicts with thrift itself
PYPI_SRC_URI_append = ";downloadfilename=${BP}.${PYPI_PACKAGE_EXT}"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"
