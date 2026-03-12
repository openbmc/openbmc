DESCRIPTION = "One version package to rule them all, One version package to find them, One version package to bring them all, and in the darkness bind them."
HOMEPAGE = "https://pypi.org/project/awesomeversion/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=92622b5a8e216099be741d78328bae5d"

SRC_URI[sha256sum] = "e6cd08c90292a11f30b8de401863dcde7bc66a671d8173f9066ebd15d9310453"

RDEPENDS:${PN} += "python3-profile python3-logging"

inherit pypi python_hatchling python_poetry_core
