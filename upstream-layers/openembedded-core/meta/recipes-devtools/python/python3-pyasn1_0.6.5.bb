SUMMARY = "Python library implementing ASN.1 types."
HOMEPAGE = "http://pyasn1.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=190f79253908c986e6cacf380c3a5f6d"

SRC_URI[sha256sum] = "6b11616a6bcf3a40e635f082bb232fa57b5e0dd7f517bd7ddd69ec3794508a47"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}:class-target += " \
    python3-codecs \
    python3-logging \
    python3-math \
    python3-shell \
"

CVE_PRODUCT = "pyasn1:pyasn1"

BBCLASSEXTEND = "native nativesdk"
