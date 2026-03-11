SUMMARY = "Joblib is a set of tools to provide lightweight pipelining in Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e481820abf0a70a18011a30153df066"

inherit python_setuptools_build_meta pypi

SRC_URI[sha256sum] = "f4f86e351f39fe3d0d32a9f2c3d8af1ee4cec285aafcb27003dda5205576b444"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-json \
    python3-multiprocessing \
    python3-pprint \
    python3-pydoc \
"
