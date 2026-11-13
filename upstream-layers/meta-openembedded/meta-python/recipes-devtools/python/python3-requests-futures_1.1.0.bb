SUMMARY = "Small add-on for the python requests http library. Makes use of python 3.2’s concurrent.futures or the backport for prior versions of python."
HOMEPAGE = "https://github.com/ross/requests-futures"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e1e50798d0afe0e1f87594c6619a2fa5"

SRC_URI[sha256sum] = "f839cf536e6638a6e0ca1cfd54ac761bf3bb2b5a7ecf9349557e53acc5244d36"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-requests \
"
