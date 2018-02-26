SUMMARY  = "Ultra fast JSON encoder and decoder for Python"
DESCRIPTION = "UltraJSON is an ultra fast JSON encoder and decoder written in pure C with bindings for Python 2.5+ and 3."

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;startline=8;endline=9;md5=4f369b3c3c290b4aede8796a4065e5ab"

SRC_URI[md5sum] = "42f77b0cce686dfa4da2e68480b1dd24"
SRC_URI[sha256sum] = "f66073e5506e91d204ab0c614a148d5aa938bdbf104751be66f8ad7a222f5f86"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
    "
