SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=5a8193ba89f1083617f32d522f6485e9"

SRC_URI[md5sum] = "0f9684622125a527fa4fbe2587738973"
SRC_URI[sha256sum] = "264a028e059c1b557701ae7c567cdab330dbd228ff924489343efcb39bd828a0"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    "    
