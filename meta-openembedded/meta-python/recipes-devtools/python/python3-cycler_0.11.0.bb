SUMMARY = "Composable style cycles"
HOMEPAGE = "http://github.com/matplotlib/cycler"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7713fe42cd766b15c710e19392bfa811"

SRC_URI[sha256sum] = "9c87405839a19696e837b3b818fed3f5f69f16f1eec1a1ad77e043dcea9c772f"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-core \
    python3-six \
"

BBCLASSEXTEND = "native nativesdk"
