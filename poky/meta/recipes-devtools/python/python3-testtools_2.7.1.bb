SUMMARY = "Extensions to the Python standard library unit testing framework"
HOMEPAGE = "https://pypi.org/project/testtools/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e2c9d3e8ba7141c83bfef190e0b9379a"

DEPENDS += "python3-hatch-vcs-native"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "df6de96010e29ee21f637a147eabf30d50b25e3841dd1d68f93ee89ce77e366c"

RDEPENDS:${PN} += "\
    python3-doctest \
    python3-extras \
    python3-six \
    "

BBCLASSEXTEND = "nativesdk"

