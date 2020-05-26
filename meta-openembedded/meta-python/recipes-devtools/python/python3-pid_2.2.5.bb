SUMMARY = "Pidfile featuring stale detection and file-locking, can also \
be used as context-manager or decorator"
HOMEPAGE = "https://github.com/trbs/pid/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=83d53cbd3105063f20305bc313464e29"

SRC_URI[md5sum] = "ad352ee1dc28b9746a15451c0c53e9d7"
SRC_URI[sha256sum] = "96eb7dba326b88f5164bc1afdc986c7793e0d32d7f62366256a3903c7b0614ef"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-fcntl ${PYTHON_PN}-logging ${PYTHON_PN}-io"
