SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=3773e39c491bb1f9ccc98389d57169d7"

SRC_URI[md5sum] = "cdef596f727cb6b4176c89769d2d41ee"
SRC_URI[sha256sum] = "946b2c837f889971df3575af9dadd8f09bfe48549ebc1b9f6434263b653707ce"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    "    
