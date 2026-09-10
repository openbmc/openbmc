SUMMARY = "Reusable constraint types to use with typing.Annotated"
DESCRIPTION = ""
HOMEPAGE = ""
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6afb13fdc220497ee5cded1e717ed67"

SRC_URI[sha256sum] = "13b2beaad985e05e2d6407ee4c4f35590b11f8d693a258a561055cac8f64cab7"

S = "${UNPACKDIR}/annotated_types-${PV}"
PYPI_PACKAGE = "annotated_types"

inherit pypi python_hatchling

RDEPENDS:${PN} = "python3-typing-extensions"

BBCLASSEXTEND = "native nativesdk"
