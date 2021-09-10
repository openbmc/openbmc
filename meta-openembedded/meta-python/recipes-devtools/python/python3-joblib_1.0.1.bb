SUMMARY = "Joblib is a set of tools to provide lightweight pipelining in Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=32b289008fb813a27c9025f02b59d03d"

inherit setuptools3 pypi

SRC_URI[md5sum] = "9914b330916395c0a1feca54949867a3"
SRC_URI[sha256sum] = "9c17567692206d2f3fb9ecf5e991084254fe631665c450b443761c4186a613f7"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-distutils \
    python3-json \
    python3-multiprocessing \
    python3-pprint \
    python3-pydoc \
"
