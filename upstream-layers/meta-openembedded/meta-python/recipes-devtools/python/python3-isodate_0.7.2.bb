SUMMARY = "ISO 8601 date/time parser"
DESCRIPTION = "An ISO 8601 date/time/duration parser and formatter"
HOMEPAGE = "https://github.com/gweis/isodate/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f15bda312de78bcdcac1df0dc536f48"

SRC_URI[sha256sum] = "4cd1aa0f43ca76f4a6c6c0292a85f40b35ec2e43e315b59f06e6d32171a953e6"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"
