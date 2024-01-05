SUMMARY = "Reusable constraint types to use with typing.Annotated"
DESCRIPTION = ""
HOMEPAGE = ""
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6afb13fdc220497ee5cded1e717ed67"

SRC_URI[sha256sum] = "563339e807e53ffd9c267e99fc6d9ea23eb8443c08f112651963e24e22f84a5d"

S = "${WORKDIR}/annotated_types-${PV}"
PYPI_PACKAGE = "annotated_types"

inherit pypi python_hatchling

RDEPENDS:${PN} = "python3-typing-extensions"
