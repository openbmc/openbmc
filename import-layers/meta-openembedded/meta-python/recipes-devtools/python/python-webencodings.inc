SUMMARY = "Character encoding aliases for legacy web content"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

SRC_URI[md5sum] = "32f6e261d52e57bf7e1c4d41546d15b8"
SRC_URI[sha256sum] = "b36a1c245f2d304965eb4e0a82848379241dc04b865afcc4aab16748587e1923"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-json \
"
