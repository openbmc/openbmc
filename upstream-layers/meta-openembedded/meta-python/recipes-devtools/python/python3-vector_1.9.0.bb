SUMMARY = "Python library for arrays of 2D, 3D, and Lorentz vectors"
DESCRIPTION = "\
    Vector is a Python library for 2D and 3D spatial vectors, as well as 4D space-time vectors. \
    It is especially intended for performing geometric calculations on arrays of vectors, rather \
    than one vector at a time in a Python for loop.\
"
HOMEPAGE = "https://github.com/scikit-hep/vector"
BUGTRACKER = "https://github.com/scikit-hep/vector/issues"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b9f683a957276148387db50971a7b3ef"

DEPENDS += "python3-hatch-vcs-native"

SRC_URI[sha256sum] = "72128b48b6a72af965babd63dccc5a110f0729e6404dcd8c5c75eaac31b80d84"

inherit pypi python_hatchling

RDEPENDS:${PN} += "\
    python3-numpy \
    python3-packaging \
"
