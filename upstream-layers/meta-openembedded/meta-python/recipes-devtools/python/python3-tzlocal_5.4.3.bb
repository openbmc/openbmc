SUMMARY = "Library to return tzinfo with the local timezone information"
HOMEPAGE = "https://pypi.org/project/tzlocal/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=57e0bd61643d81d05683cdce65b11d10"

SRC_URI[sha256sum] = "3a8c9bc18cf47e1dcde252ea0e6a72a6cde320a400b6ac6db1f1f8cccd553c00"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-logging \
    python3-zoneinfo \
"
