DESCRIPTION = "One version package to rule them all, One version package to find them, One version package to bring them all, and in the darkness bind them."
HOMEPAGE = "https://pypi.org/project/awesomeversion/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=92622b5a8e216099be741d78328bae5d"

SRC_URI[sha256sum] = "9146329196f0f045887de6c195730750f8f7a9302d1c149378db73ab5dc468f0"

RDEPENDS:${PN} += "python3-profile python3-logging"

inherit pypi python_poetry_core
