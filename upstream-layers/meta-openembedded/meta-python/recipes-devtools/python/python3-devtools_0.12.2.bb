SUMMARY = "Python's missing debug print command, and more."
DESCRIPTION = "Python's missing debug print command, and more."
HOMEPAGE = "https://github.com/samuelcolvin/python-devtools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3c69d58fa5f4ff0b4244368d39546751"

SRC_URI[sha256sum] = "efceab184cb35e3a11fa8e602cc4fadacaa2e859e920fc6f87bf130b69885507"

inherit pypi python_hatchling ptest-python-pytest

RDEPENDS:${PN}-ptest += "\
    python3-core \
    python3-executing \
    python3-numpy \
    python3-asttokens \
"

BBCLASSEXTEND = "native nativesdk"
