DESCRIPTION = "One version package to rule them all, One version package to find them, One version package to bring them all, and in the darkness bind them."
HOMEPAGE = "https://pypi.org/project/awesomeversion/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=92622b5a8e216099be741d78328bae5d"

SRC_URI[sha256sum] = "d64c9f3579d2f60a5aa506a9dd0b38a74ab5f45e04800f943a547c1102280f31"

RDEPENDS:${PN} += "python3-profile python3-logging"

inherit pypi python_poetry_core
