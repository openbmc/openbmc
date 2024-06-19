SUMMARY = "Joblib is a set of tools to provide lightweight pipelining in Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e481820abf0a70a18011a30153df066"

inherit setuptools3 pypi

SRC_URI[sha256sum] = "2382c5816b2636fbd20a09e0f4e9dad4736765fdfb7dca582943b9c1366b3f0e"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-json \
    python3-multiprocessing \
    python3-pprint \
    python3-pydoc \
"
