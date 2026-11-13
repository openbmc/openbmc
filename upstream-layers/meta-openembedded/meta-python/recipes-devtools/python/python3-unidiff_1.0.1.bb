SUMMARY = "Unified diff parsing/metadata extraction library"
HOMEPAGE = "https://github.com/matiasb/python-unidiff"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4c434b08ef42fea235bb019b5e5a97b3"

SRC_URI[sha256sum] = "d9425bc516390c54743a1045b2c1c97f02d245a913a241dd499f958adb2df998"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-codecs \
    python3-io \
"
