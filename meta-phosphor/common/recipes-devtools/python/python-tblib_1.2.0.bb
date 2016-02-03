SUMMARY = "Traceback fiddling library."
DESCRIPTION = "For now allows you to pickle tracebacks and raise exceptions with pickled tracebacks in different processes. This allows better error handling when running code over multiple processes (imagine multiprocessing, billiard, futures, celery etc)"
HOMEPAGE = "https://pypi.python.org/pypi/tblib"
SECTION = "devel/python"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=cbdf69ffe15ef6bf937556e126c586a5"

SRCNAME = "tblib"

SRC_URI = "https://pypi.python.org/packages/source/t/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "804aed92abafcffa7439b0ce4e5dbc95"
SRC_URI[sha256sum] = "80bb2d8782cc6f4b898e6455ac51abca3529f20205e5bcf34ae2ebf3e10df613"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
