SUMMARY = "REPL plugin for Click"
HOMEPAGE = "https://github.com/untitaker/click-repl"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fee2943fdd4d8afbac9ccc1c8ac137d5"

SRC_URI[sha256sum] = "cd12f68d745bf6151210790540b4cb064c7b13e571bc64b6957d98d120dacfd8"

inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-click"
