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

SRC_URI[sha256sum] = "58f95e9e24463851ca34176a20df2fd2e80b41d78615e5b1f7ae4bf313424ca6"

inherit pypi python_hatchling

RDEPENDS:${PN} += "\
    python3-numpy \
    python3-packaging \
"
