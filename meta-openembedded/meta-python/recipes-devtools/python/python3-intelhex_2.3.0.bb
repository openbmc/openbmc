SUMMARY = "Python library for Intel HEX files manipulations"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4eba844696655c3eae07aca8e3a94772"

SRC_URI[sha256sum] = "892b7361a719f4945237da8ccf754e9513db32f5628852785aea108dcd250093"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

