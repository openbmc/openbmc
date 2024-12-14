SUMMARY = "Pidfile featuring stale detection and file-locking, can also \
be used as context-manager or decorator"
HOMEPAGE = "https://github.com/trbs/pid/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=83d53cbd3105063f20305bc313464e29"

SRC_URI[sha256sum] = "0e33670e83f6a33ebb0822e43a609c3247178d4a375ff50a4689e266d853eb66"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-fcntl python3-logging python3-io"
