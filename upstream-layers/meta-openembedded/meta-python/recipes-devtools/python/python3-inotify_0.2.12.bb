SUMMARY = "An adapter to Linux kernel support for inotify directory-watching."
HOMEPAGE = "https://pypi.org/project/inotify/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

SRC_URI[sha256sum] = "9aee407f92c7d51a2ce50f3b78291a9094e334e34bd68e82bf60020795fa2c94"

PYPI_PACKAGE = "inotify"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-logging \
"
