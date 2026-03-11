SUMMARY = "Python library implementing ASN.1 types."
HOMEPAGE = "http://pyasn1.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=190f79253908c986e6cacf380c3a5f6d"

SRC_URI[sha256sum] = "6f580d2bdd84365380830acf45550f2511469f673cb4a5ae3857a3170128b034"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}:class-target += " \
    python3-codecs \
    python3-logging \
    python3-math \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
