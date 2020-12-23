SUMMARY = "Cross-platform colored terminal text."
HOMEPAGE = "https://github.com/tartley/colorama"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b4936429a56a652b84c5c01280dcaa26"

inherit pypi setuptools3

SRC_URI[md5sum] = "57b22f2597f63df051b69906fbf310cc"
SRC_URI[sha256sum] = "5941b2b48a20143d2267e95b1c2a7603ce057ee39fd88e7329b0c292aa16869b"

BBCLASSEXTEND = "native nativesdk"
