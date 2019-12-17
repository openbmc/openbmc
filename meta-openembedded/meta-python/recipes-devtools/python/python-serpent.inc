SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=5cd70632b6cdb96df9ddaf6a4ce619e6"

SRC_URI[md5sum] = "15ef8b67c76a6d19bac9c16731a1e62a"
SRC_URI[sha256sum] = "f306336ca09aa38e526f3b03cab58eb7e45af09981267233167bcf3bfd6436ab"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    "    
