SUMMARY = "Python HTTP for Humans."
HOMEPAGE = "http://python-requests.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI[sha256sum] = "942c5a758f98d790eaed1a29cb6eefc7ffb0d1cf7af05c3d2791656dbd6ad1e1"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-email \
    python3-json \
    python3-ndg-httpsclient \
    python3-netserver \
    python3-pyasn1 \
    python3-pyopenssl \
    python3-pysocks \
    python3-urllib3 \
    python3-chardet \
    python3-idna \
    python3-compression \
"

CVE_PRODUCT = "requests"

BBCLASSEXTEND = "native nativesdk"
