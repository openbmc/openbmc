SUMMARY = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
HOMEPAGE = "https://pypi.org/project/ruamel.yaml/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e5f41db57c3a8d3584b450d35985ad1"

PYPI_PACKAGE = "ruamel.yaml"

inherit pypi setuptools3

SRC_URI[sha256sum] = "801046a9caacb1b43acc118969b49b96b65e8847f29029563b29ac61d02db61b"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-netclient \
"

do_install:prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}

BBCLASSEXTEND = "native nativesdk"
