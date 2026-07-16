SUMMARY = "Library to return tzinfo with the local timezone information"
HOMEPAGE = "https://pypi.org/project/tzlocal/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=57e0bd61643d81d05683cdce65b11d10"

SRC_URI[sha256sum] = "8dbb8660838688a7b6ba4fed31d18dedf842afb4d47ca050d6d891c2c15f3be4"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-logging \
    python3-zoneinfo \
"
