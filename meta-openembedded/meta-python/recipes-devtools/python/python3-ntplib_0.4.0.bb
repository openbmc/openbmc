DESCRIPTION = "This module offers a simple interface to query NTP servers from Python."
HOMEPAGE = "https://github.com/cf-natali/ntplib"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=40707636fe237d725e2bd32cd949b7fe"

SRC_URI[sha256sum] = "899d8fb5f8c2555213aea95efca02934c7343df6ace9d7628a5176b176906267"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-datetime python3-io"
