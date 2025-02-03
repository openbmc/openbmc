SUMMARY = "Simple construction, analysis and modification of binary data."
HOMEPAGE = "https://github.com/scott-griffiths/bitstring"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=963a24c175e658fbf16a764135121ffa"

SRC_URI[sha256sum] = "81800bc4e00b6508716adbae648e741256355c8dfd19541f76482fb89bee0313"

PYPI_PACKAGE = "bitstring"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "\
    python3-core \
    python3-io \
    python3-mmap \
    python3-numbers \
    python3-bitarray \
"

BBCLASSEXTEND = "native nativesdk"
