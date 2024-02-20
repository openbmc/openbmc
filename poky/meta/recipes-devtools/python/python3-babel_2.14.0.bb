SUMMARY = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f97d9a63e91407b4c0d01efde91cfc0"

SRC_URI[sha256sum] = "6919867db036398ba21eb5c7a0f6b28ab8cbc3ae7a73a44ebe34ae74a4e7d363"

PYPI_PACKAGE = "Babel"

inherit pypi setuptools3

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
