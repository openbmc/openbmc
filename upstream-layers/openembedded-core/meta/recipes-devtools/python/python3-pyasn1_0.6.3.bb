SUMMARY = "Python library implementing ASN.1 types."
HOMEPAGE = "http://pyasn1.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=190f79253908c986e6cacf380c3a5f6d"

SRC_URI[sha256sum] = "697a8ecd6d98891189184ca1fa05d1bb00e2f84b5977c481452050549c8a72cf"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}:class-target += " \
    python3-codecs \
    python3-logging \
    python3-math \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
