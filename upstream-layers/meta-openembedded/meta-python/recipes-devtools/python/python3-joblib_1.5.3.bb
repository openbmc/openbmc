SUMMARY = "Joblib is a set of tools to provide lightweight pipelining in Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2e481820abf0a70a18011a30153df066"

inherit python_setuptools_build_meta pypi

SRC_URI[sha256sum] = "8561a3269e6801106863fd0d6d84bb737be9e7631e33aaed3fb9ce5953688da3"

CVE_PRODUCT = "joblib"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-json \
    python3-multiprocessing \
    python3-pprint \
    python3-pydoc \
"
