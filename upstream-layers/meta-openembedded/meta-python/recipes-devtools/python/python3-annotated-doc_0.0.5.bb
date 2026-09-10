SUMMARY = "Document parameters, class attributes, return types, and variables inline, with Annotated."
HOMEPAGE = "https://github.com/fastapi/annotated-doc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e36e91f278975b8bb76a769f32582892"

SRC_URI[sha256sum] = "c7e58ce09192557605d8bbd92836d7e1d520ac9580096042c0bfd197efacf1bb"

inherit pypi python_pdm

PYPI_PACKAGE = "annotated_doc"

RDEPENDS:${PN} += "python3-compression"
