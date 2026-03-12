SUMMARY = "Document parameters, class attributes, return types, and variables inline, with Annotated."
HOMEPAGE = "https://github.com/fastapi/annotated-doc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e36e91f278975b8bb76a769f32582892"

SRC_URI[sha256sum] = "fbcda96e87e9c92ad167c2e53839e57503ecfda18804ea28102353485033faa4"

UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_pdm

PYPI_PACKAGE = "annotated_doc"

RDEPENDS:${PN} += "python3-compression"
