SUMMARY = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e83509a66c69efcb4f3a7d4f27fd0693"

SRC_URI[sha256sum] = "0c54cffb19f690cdcc52a3b50bcbf71e07a808d1c80d549f2459b9d2cf0afb9d"

inherit pypi setuptools3

S = "${UNPACKDIR}/babel-${PV}"

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    python3-codecs \
    python3-difflib \
    python3-netserver \
    python3-numbers \
    python3-pickle \
    python3-pytz \
    python3-setuptools \
    python3-shell \
    python3-threading \
"

BBCLASSEXTEND = "native nativesdk"
