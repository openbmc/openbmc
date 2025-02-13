SUMMARY = "More routines for operating on iterables, beyond itertools"
HOMEPAGE = "https://github.com/erikrose/more-itertools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "2cd7fad1009c31cc9fb6a035108509e6547547a7a738374f10bd49a09eb3ee3b"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += " \
        python3-asyncio \
        "

RDEPENDS:${PN}-ptest += " \
	python3-statistics \
        "

BBCLASSEXTEND = "native nativesdk"
