SUMMARY = "Pidfile featuring stale detection and file-locking, can also \
be used as context-manager or decorator"
HOMEPAGE = "https://github.com/trbs/pid/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=83d53cbd3105063f20305bc313464e29"

SRC_URI[md5sum] = "5c011ebebbdfd529f6e85d2e0396dae8"
SRC_URI[sha256sum] = "925b61c35b6f2bc6b43075f493e99792f1473575a0beeb85bcf7de1d6a4a3c7d"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-fcntl ${PYTHON_PN}-logging ${PYTHON_PN}-io"
