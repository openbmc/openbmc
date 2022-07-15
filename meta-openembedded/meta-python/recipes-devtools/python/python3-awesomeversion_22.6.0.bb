DESCRIPTION = "One version package to rule them all, One version package to find them, One version package to bring them all, and in the darkness bind them."
HOMEPAGE = "https://pypi.org/project/awesomeversion/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=92622b5a8e216099be741d78328bae5d"

SRC_URI[sha256sum] = "38f580bfacf1c06b674bcd0f68e0c445ebb03fcd3700c6a2c588fb9313308e0f"

RDEPENDS:${PN} += "python3-profile python3-logging"

inherit pypi setuptools3
