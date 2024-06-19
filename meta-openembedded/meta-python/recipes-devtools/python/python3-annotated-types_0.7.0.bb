SUMMARY = "Reusable constraint types to use with typing.Annotated"
DESCRIPTION = ""
HOMEPAGE = ""
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6afb13fdc220497ee5cded1e717ed67"

SRC_URI[sha256sum] = "aff07c09a53a08bc8cfccb9c85b05f1aa9a2a6f23728d790723543408344ce89"

S = "${WORKDIR}/annotated_types-${PV}"
PYPI_PACKAGE = "annotated_types"

inherit pypi python_hatchling

RDEPENDS:${PN} = "python3-typing-extensions"
