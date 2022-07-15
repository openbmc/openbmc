SUMMARY = "A robust email address syntax and deliverability validation library."
SECTION = "devel/python"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d3616852dbf7b1a6d4b53b00626032"

SRC_URI[sha256sum] = "6757aea012d40516357c0ac2b1a4c31219ab2f899d26831334c5d069e8b6c3d8"

PYPI_PACKAGE = "email_validator"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-dnspython python3-idna"
