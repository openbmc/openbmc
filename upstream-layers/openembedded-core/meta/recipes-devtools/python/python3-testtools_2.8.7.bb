SUMMARY = "Extensions to the Python standard library unit testing framework"
HOMEPAGE = "https://pypi.org/project/testtools/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6fd7d57d2bdc8678de824fb404cb1a4"

DEPENDS += "python3-hatch-vcs-native"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "1aa19aaabf9736385fc111a95f94f6b0661c0b41e65ca5129223eb518c63e8b9"

RDEPENDS:${PN} += "\
    python3-compression \
    python3-doctest \
    python3-extras \
    python3-json \
    python3-six \
    "

BBCLASSEXTEND = "nativesdk"

