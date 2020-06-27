DESCRIPTION = "A utility belt for advanced users of python-requests."
HOMEPAGE = "https://toolbelt.readthedocs.org"
AUTHOR = "Ian Cordasco, Cory Benfield"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71760e0f1dda8cff91b0bc9246caf571"

SRC_URI[md5sum] = "b1509735c4b4cf95df2619facbc3672e"
SRC_URI[sha256sum] = "968089d4584ad4ad7c171454f0a5c6dac23971e9472521ea3b6d49d610aa6fc0"

inherit setuptools3
inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-requests (>=2.0.1) \
"
