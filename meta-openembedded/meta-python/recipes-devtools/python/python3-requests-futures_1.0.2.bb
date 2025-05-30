SUMMARY = "Small add-on for the python requests http library. Makes use of python 3.2’s concurrent.futures or the backport for prior versions of python."
HOMEPAGE = "https://github.com/ross/requests-futures"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e1e50798d0afe0e1f87594c6619a2fa5"

SRC_URI[sha256sum] = "6b7eb57940336e800faebc3dab506360edec9478f7b22dc570858ad3aa7458da"

PYPI_PACKAGE = "requests_futures"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-requests \
"

