SUMMARY = "Simple Python module for working with HTML/CSS color definitions."
HOMEPAGE = "https://pypi.org/project/webcolors/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cbaebec43b7d199c7fd8f5411b3b0448"

SRC_URI[sha256sum] = "62abae86504f66d0f6364c2a8520de4a0c47b80c03fc3a5f1815fedbef7c19bf"

inherit pypi python_pdm ptest-python-pytest

RDEPENDS:${PN}:class-target = "\
    python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
