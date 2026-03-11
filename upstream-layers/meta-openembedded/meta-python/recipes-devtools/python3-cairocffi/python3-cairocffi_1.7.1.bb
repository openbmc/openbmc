SUMMARY = "cairocffi is a CFFI-based drop-in replacement for Pycairo, a set of Python bindings and object-oriented API for cairo."
HOMEPAGE = "https://github.com/Kozea/cairocffi"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7c7639c2f7e3d6103414416614bfaac"

SRC_URI[sha256sum] = "2e48ee864884ec4a3a34bfa8c9ab9999f688286eb714a15a43ec9d068c36557b"

inherit pypi python_flit_core

RDEPENDS:${PN} = "\
    python3-cffi \
"
