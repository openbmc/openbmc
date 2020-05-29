DESCRIPTION = "Universal Binary JSON encoder/decoder"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=383b9e6c3f9d1386a4eea17792291d91"

SRC_URI[sha256sum] = "b9bfb8695a1c7e3632e800fb83c943bf67ed45ddd87cd0344851610c69a5a482"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
