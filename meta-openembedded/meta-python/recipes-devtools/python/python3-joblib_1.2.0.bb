SUMMARY = "Joblib is a set of tools to provide lightweight pipelining in Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e481820abf0a70a18011a30153df066"

inherit setuptools3 pypi

SRC_URI[sha256sum] = "e1cee4a79e4af22881164f218d4311f60074197fb707e082e803b61f6d137018"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-distutils \
    python3-json \
    python3-multiprocessing \
    python3-pprint \
    python3-pydoc \
"
