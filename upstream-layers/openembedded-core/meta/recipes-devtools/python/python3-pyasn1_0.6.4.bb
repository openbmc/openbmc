SUMMARY = "Python library implementing ASN.1 types."
HOMEPAGE = "http://pyasn1.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=190f79253908c986e6cacf380c3a5f6d"

SRC_URI[sha256sum] = "9c447d8431c947fe4c8febc4ed9e760bc29011a5b01e5c74b67025bd9fb8ce81"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}:class-target += " \
    python3-codecs \
    python3-logging \
    python3-math \
    python3-shell \
"

CVE_PRODUCT = "pyasn1:pyasn1"

BBCLASSEXTEND = "native nativesdk"
