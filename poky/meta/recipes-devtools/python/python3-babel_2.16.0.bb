SUMMARY = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7ebed8cd9f4ff11f547e3afd024a28c"

SRC_URI[sha256sum] = "d1f3554ca26605fe173f3de0c65f750f5a42f924499bf134de6423582298e316"

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
