DESCRIPTION = "Human friendly output for text interfaces using Python"
HOMEPAGE = "https://humanfriendly.readthedocs.io/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5d178009f806c2bdd498a19be0013a7a"

PYPI_PACKAGE = "humanfriendly"

SRC_URI[sha256sum] = "6b0b831ce8f15f7300721aa49829fc4e83921a9a301cc7f606be6686a2288ddc"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target += " \
    python3-datetime \
    python3-fcntl \
    python3-io \
    python3-logging \
    python3-math \
    python3-numbers \
    python3-shell \
    python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
