SUMMARY = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
HOMEPAGE = "https://pypi.org/project/ruamel.yaml/"
AUTHOR = "Anthon van der Neut"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=034154b7344d15438bc5ed5ee9cc075f"

PYPI_PACKAGE = "ruamel.yaml"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8b7ce697a2f212752a35c1ac414471dc16c424c9573be4926b56ff3f5d23b7af"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-netclient \
"

do_install:prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}

BBCLASSEXTEND = "native nativesdk"
