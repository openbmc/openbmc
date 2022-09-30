SUMMARY = "A robust email address syntax and deliverability validation library."
SECTION = "devel/python"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d3616852dbf7b1a6d4b53b00626032"

SRC_URI[sha256sum] = "553a66f8be2ec2dea641ae1d3f29017ab89e9d603d4a25cdaac39eefa283d769"

PYPI_PACKAGE = "email_validator"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-dnspython python3-idna"
