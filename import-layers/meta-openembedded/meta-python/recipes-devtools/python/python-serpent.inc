SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=9f611d4c9e2816d387bab64c40d93294"

SRC_URI[md5sum] = "d9d6afa51f2560cf5933dcdfd947011d"
SRC_URI[sha256sum] = "8480ce3d8864b8974d9fe396998c6a7dae96edb68edf905bfd32ea9a11853ba5"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    "    
