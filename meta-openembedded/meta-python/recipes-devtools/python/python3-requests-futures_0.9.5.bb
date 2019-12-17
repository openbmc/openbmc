SUMMARY = "Small add-on for the python requests http library. Makes use of python 3.2’s concurrent.futures or the backport for prior versions of python."
HOMEPAGE = "https://github.com/ross/requests-futures"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e1e50798d0afe0e1f87594c6619a2fa5"

SRC_URI[md5sum] = "e20dc6f063c70888a7f8225e349b6682"
SRC_URI[sha256sum] = "33aa8a3b7892850701707d7e094b1e1ce7c4f7a36ff2a1dcc2da4e01a1a00f7e"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    python3-requests \
"

