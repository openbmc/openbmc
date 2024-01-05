SUMMARY = "Composable style cycles"
HOMEPAGE = "http://github.com/matplotlib/cycler"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7713fe42cd766b15c710e19392bfa811"

SRC_URI[sha256sum] = "88bb128f02ba341da8ef447245a9e138fae777f6a23943da4540077d3601eb1c"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-core \
    python3-six \
"

BBCLASSEXTEND = "native nativesdk"
