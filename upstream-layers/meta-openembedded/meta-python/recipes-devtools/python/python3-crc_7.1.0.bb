SUMMARY = "Library and CLI to calculate and verify all kinds of CRC checksums"
DESCRIPTION = "\
    Calculate CRC checksums, verify CRC checksum, predefined CRC configurations, \
    custom CRC configurations"
BUGTRACKER = "https://github.com/Nicoretti/crc/issues"
HOMEPAGE = "https://nicoretti.github.io/crc/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f94c07350a9f2e0ce3a246fed3b32353"

SRC_URI[sha256sum] = "99dd540909a37ae4f62c65441df8ecb4e7f9af014fecaf4f331052a41d66c07d"

inherit pypi python_poetry_core
