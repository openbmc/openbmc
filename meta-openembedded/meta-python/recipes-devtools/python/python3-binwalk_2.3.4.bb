SUMMARY = "Firmware analysis tool"
DESCRIPTION = "This package contains Python Binwalk tool. Binwalk is a fast, \
easy to use tool for analyzing, reverse engineering, and extracting firmware \
images."
HOMEPAGE = "https://github.com/ReFirmLabs/binwalk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65bbee055d3ea3bfc475f07aecf4de64"

SRC_URI = "git://github.com/ReFirmLabs/binwalk;protocol=https;branch=master"

SRCREV = "cddfede795971045d99422bd7a9676c8803ec5ee"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

inherit setuptools3

RDEPENDS:${PN} += " \
    python3-compression \
    python3-logging \
    python3-netserver \
    python3-setuptools \
    python3-stringold \
"
