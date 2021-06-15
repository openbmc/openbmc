SUMMARY = "Composable style cycles"
HOMEPAGE = "http://github.com/matplotlib/cycler"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7713fe42cd766b15c710e19392bfa811"

SRC_URI[md5sum] = "4cb42917ac5007d1cdff6cccfe2d016b"
SRC_URI[sha256sum] = "cd7b2d1018258d7247a71425e9f26463dfb444d411c39569972f4ce586b0c9d8"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    python3-core \
    python3-six \
"

BBCLASSEXTEND = "native nativesdk"
