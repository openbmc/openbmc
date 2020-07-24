SUMMARY = "Pure-Python RSA implementation"
SECTION = "devel/python"
AUTHOR = "Sybren A. Stuvel"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c403f6882d4f97a9cd927df987d55634"

SRC_URI[sha256sum] = "25df4e10c263fb88b5ace923dd84bf9aa7f5019687b5e55382ffcdb8bede9db5"

inherit pypi setuptools3 update-alternatives

ALTERNATIVE_${PN} = "\
    pyrsa-decrypt \
    pyrsa-decrypt-bigfile \
    pyrsa-encrypt \
    pyrsa-encrypt-bigfile \
    pyrsa-keygen \
    pyrsa-priv2pub \
    pyrsa-sign \
    pyrsa-verify \
"

ALTERNATIVE_LINK_NAME[pyrsa-decrypt] = "${bindir}/pyrsa-decrypt"
ALTERNATIVE_LINK_NAME[pyrsa-decrypt-bigfile] = "${bindir}/pyrsa-decrypt-bigfile"
ALTERNATIVE_LINK_NAME[pyrsa-encrypt] = "${bindir}/pyrsa-encrypt"
ALTERNATIVE_LINK_NAME[pyrsa-encrypt-bigfile] = "${bindir}/pyrsa-encrypt-bigfile"
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
