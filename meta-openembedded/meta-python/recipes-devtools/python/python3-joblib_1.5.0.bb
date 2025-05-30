SUMMARY = "Joblib is a set of tools to provide lightweight pipelining in Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e481820abf0a70a18011a30153df066"

inherit python_setuptools_build_meta pypi

SRC_URI[sha256sum] = "d8757f955389a3dd7a23152e43bc297c2e0c2d3060056dad0feefc88a06939b5"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-json \
    python3-multiprocessing \
    python3-pprint \
    python3-pydoc \
"
