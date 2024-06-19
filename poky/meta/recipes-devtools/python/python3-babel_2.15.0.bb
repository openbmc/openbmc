SUMMARY = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7ebed8cd9f4ff11f547e3afd024a28c"

SRC_URI[sha256sum] = "8daf0e265d05768bc6c7a314cf1321e9a123afc328cc635c18622a2f30a04413"

PYPI_PACKAGE = "Babel"

PYPI_SRC_URI = "https://files.pythonhosted.org/packages/source/b/babel/babel-${PV}.tar.gz"

inherit pypi setuptools3

S = "${WORKDIR}/babel-${PV}"

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
