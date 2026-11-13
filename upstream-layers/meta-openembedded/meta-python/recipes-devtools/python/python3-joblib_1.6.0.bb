SUMMARY = "Joblib is a set of tools to provide lightweight pipelining in Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e481820abf0a70a18011a30153df066"

inherit python_setuptools_build_meta pypi

SRC_URI[sha256sum] = "2ccc96785b12046c08fd6d55839c12857831b54a3c1673ffadd2f04bfc4eda03"

CVE_PRODUCT = "joblib"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-json \
    python3-multiprocessing \
    python3-pprint \
    python3-pydoc \
    python3-cloudpickle \
"
