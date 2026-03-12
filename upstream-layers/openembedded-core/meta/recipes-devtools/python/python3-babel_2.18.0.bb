SUMMARY = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a2b86daab39fdb8ce59d97e9fecc90ee"

SRC_URI[sha256sum] = "b80b99a14bd085fcacfa15c9165f651fbb3406e66cc603abf11c5750937c992d"

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
