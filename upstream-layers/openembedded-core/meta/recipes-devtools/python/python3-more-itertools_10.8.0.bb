SUMMARY = "More routines for operating on iterables, beyond itertools"
HOMEPAGE = "https://github.com/erikrose/more-itertools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "f638ddf8a1a0d134181275fb5d58b086ead7c6a72429ad725c67503f13ba30bd"

PYPI_PACKAGE = "more_itertools"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += " \
        python3-asyncio \
        "

RDEPENDS:${PN}-ptest += " \
	python3-statistics \
        "

BBCLASSEXTEND = "native nativesdk"
