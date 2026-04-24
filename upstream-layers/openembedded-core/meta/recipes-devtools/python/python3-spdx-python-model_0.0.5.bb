SUMMARY = "Generated Python code for SPDX Spec version 3"
HOMEPAGE = "https://pypi.org/project/spdx-python-model/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PYPI_PACKAGE = "spdx_python_model"
SRC_URI[sha256sum] = "4bcf7c6e5e2e8f0b787ed4eb8fb519e2ed776e820cb6d9eb93e44e98eb92ca2d"

SRC_URI += " \
    https://spdx.org/rdf/3.0.1/spdx-context.jsonld;name=spdx1 \
    https://spdx.org/rdf/3.0.1/spdx-json-serialize-annotations.ttl;name=spdx2 \
    https://spdx.org/rdf/3.0.1/spdx-model.ttl;name=spdx3 \
"

SRC_URI[spdx1.sha256sum] = "c72b0928f094c83e5c127784edb1ebca2af74a104fcacc007c332b23cbc788bd"
SRC_URI[spdx2.sha256sum] = "c6a54b51230eb2bf3b31302546af201f303e0b7931c1db404d7f5b72b6f863e6"
SRC_URI[spdx3.sha256sum] = "30ebb4af2d70a9809044ef46f44cc3dc5125226d70f818a50ed2e1d5f404c593"

inherit pypi python_hatchling

export SHACL2CODE_SPDX_DIR = "${S}/spdx"

do_configure:append() {
    mkdir -p "${SHACL2CODE_SPDX_DIR}/3.0.1/"
    cp ${UNPACKDIR}/spdx-context.jsonld "${SHACL2CODE_SPDX_DIR}/3.0.1/"
    cp ${UNPACKDIR}/spdx-json-serialize-annotations.ttl "${SHACL2CODE_SPDX_DIR}/3.0.1/"
    cp ${UNPACKDIR}/spdx-model.ttl "${SHACL2CODE_SPDX_DIR}/3.0.1/"
}

DEPENDS += " \
    python3-shacl2code-native \
    python3-hatch-build-scripts-native \
"

BBCLASSEXTEND = "native nativesdk"
