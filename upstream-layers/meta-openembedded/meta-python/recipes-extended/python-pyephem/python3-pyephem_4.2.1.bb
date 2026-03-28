SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "https://rhodesmill.org/pyephem/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c930b395b435b00bb13ec83b0c99f40"

SRC_URI[sha256sum] = "920cb30369c79fde1088c2060d555ea5f8a50fdc80a9265832fd5bf195cf147f"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-math \
    "
