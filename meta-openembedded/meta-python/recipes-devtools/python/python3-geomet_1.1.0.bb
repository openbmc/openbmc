SUMMARY = "Convert GeoJSON to WKT/WKB (Well-Known Text/Binary), and vice versa."
DESCRIPTION = "Convert GeoJSON to WKT/WKB (Well-Known Text/Binary), and vice versa."
HOMEPAGE = "https://github.com/geomet/geomet"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"
SRCNAME = "geomet"

SRC_URI[sha256sum] = "cb52411978ee01ff104ab48f108d7333b14423ae7a15a65fee25b7d29bda2e1b"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/geomet/geomet.git;protocol=https;branch=master"
SRCREV = "6ac73c312b52aca328db2e61d90c5e363b62639f"

inherit setuptools3
PIP_INSTALL_PACKAGE = "geomet"

RDEPENDS:${PN} += "\
    python3-click \
    python3-core \
    python3-io \
    python3-json \
    python3-logging \
    python3-six \
"
