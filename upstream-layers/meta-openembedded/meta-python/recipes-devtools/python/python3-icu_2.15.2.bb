SUMMARY = "Python extension wrapping the ICU C++ API"
HOMEPAGE = "https://gitlab.pyicu.org/main/pyicu"
BUGTRACKER = "https://gitlab.pyicu.org/main/pyicu/-/issues"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0724597863f1581ab01429e0423e779f"

DEPENDS += "icu"

PYPI_PACKAGE = "pyicu"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "561e77eedff17cec6839f26211f7a5ce3c071b776e8a0ec9d1207f46cbce598f"

SRC_URI += "file://0001-Fix-host-contamination-of-include-files.patch \
            file://0001-Remove-assert-checking-for-trivial-type.patch"

inherit pkgconfig pypi python_setuptools_build_meta

# it's lowercase pyicu instead of ${PYPI_PACKAGE} in this version
S = "${UNPACKDIR}/pyicu-${PV}"
