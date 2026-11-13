SUMMARY = "RADIUS tools"
DESCRIPTION = "Python RADIUS Implementation"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=c97c15f70e2734c8ce7c0d636e662aa8"

SRC_URI[sha256sum] = "26d23ca80cde9f50d5ef0167d4c7f2bbfe218d85e207f2c0ca31dddd00a5deee"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-io \
    python3-logging \
    python3-netaddr \
"

CVE_PRODUCT = "pyrad"
