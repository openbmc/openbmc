SUMMARY = "Extensions to the Python standard library unit testing framework"
HOMEPAGE = "https://pypi.org/project/testtools/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e2c9d3e8ba7141c83bfef190e0b9379a"

inherit pypi

SRC_URI[sha256sum] = "df6de96010e29ee21f637a147eabf30d50b25e3841dd1d68f93ee89ce77e366c"

DEPENDS += " \
    python3-pbr \
    "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
    python3-pbr-native \
    "

RDEPENDS:${PN} += "\
    python3-doctest \
    python3-extras \
    python3-pbr \
    python3-six \
    "

BBCLASSEXTEND = "nativesdk"
