DESCRIPTION = "One version package to rule them all, One version package to find them, One version package to bring them all, and in the darkness bind them."
HOMEPAGE = "https://pypi.org/project/awesomeversion/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=92622b5a8e216099be741d78328bae5d"

SRC_URI[sha256sum] = "2f4190d333e81e10b2a4e156150ddb3596f5f11da67e9d51ba39057aa7a17f7e"

RDEPENDS:${PN} += "python3-profile python3-logging"

inherit pypi setuptools3
