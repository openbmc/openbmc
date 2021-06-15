SUMMARY = "Small add-on for the python requests http library. Makes use of python 3.2’s concurrent.futures or the backport for prior versions of python."
HOMEPAGE = "https://github.com/ross/requests-futures"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e1e50798d0afe0e1f87594c6619a2fa5"

SRC_URI[md5sum] = "601b5e90413bb00e06148752d31f0cc7"
SRC_URI[sha256sum] = "35547502bf1958044716a03a2f47092a89efe8f9789ab0c4c528d9c9c30bc148"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    python3-requests \
"

