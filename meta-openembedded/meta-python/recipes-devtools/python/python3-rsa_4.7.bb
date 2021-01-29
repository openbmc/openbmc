SUMMARY = "Pure-Python RSA implementation"
SECTION = "devel/python"
AUTHOR = "Sybren A. Stuvel"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c403f6882d4f97a9cd927df987d55634"

SRC_URI[sha256sum] = "69805d6b69f56eb05b62daea3a7dbd7aa44324ad1306445e05da8060232d00f4"

inherit pypi setuptools3 update-alternatives

ALTERNATIVE_${PN} = "\
    pyrsa-decrypt \
    pyrsa-encrypt \
    pyrsa-keygen \
    pyrsa-priv2pub \
    pyrsa-sign \
    pyrsa-verify \
"

ALTERNATIVE_LINK_NAME[pyrsa-decrypt] = "${bindir}/pyrsa-decrypt"
ALTERNATIVE_LINK_NAME[pyrsa-encrypt] = "${bindir}/pyrsa-encrypt"
ALTERNATIVE_LINK_NAME[pyrsa-keygen] = "${bindir}/pyrsa-keygen"
ALTERNATIVE_LINK_NAME[pyrsa-priv2pub] = "${bindir}/pyrsa-priv2pub"
ALTERNATIVE_LINK_NAME[pyrsa-sign] = "${bindir}/pyrsa-sign"
ALTERNATIVE_LINK_NAME[pyrsa-verify] = "${bindir}/pyrsa-verify"
ALTERNATIVE_PRIORITY = "30"


RDEPENDS_${PN} += "\
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pickle \
"

RDEPENDS_${PN} += "${PYTHON_PN}-pyasn1"
