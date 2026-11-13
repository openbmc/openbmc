SUMMARY = "Library and CLI to calculate and verify all kinds of CRC checksums"
DESCRIPTION = "\
    Calculate CRC checksums, verify CRC checksum, predefined CRC configurations, \
    custom CRC configurations"
BUGTRACKER = "https://github.com/Nicoretti/crc/issues"
HOMEPAGE = "https://nicoretti.github.io/crc/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f94c07350a9f2e0ce3a246fed3b32353"

SRC_URI[sha256sum] = "e1ef893b042c26c9f47fd32f3c17507b7d1bd81169acebaef908774856fdaa3e"

inherit pypi python_uv_build python_hatchling
