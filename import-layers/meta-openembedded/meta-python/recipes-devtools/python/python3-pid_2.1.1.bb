SUMMARY = "Pidfile featuring stale detection and file-locking, can also \
be used as context-manager or decorator"
HOMEPAGE = "https://github.com/trbs/pid/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=83d53cbd3105063f20305bc313464e29"

SRC_URI[md5sum] = "9634b1e3d545544a9d496e25e4530d14"
SRC_URI[sha256sum] = "b443169d3dc21397695b4a82016fadb4cfdb0ed8b2ddb4aaa428e1701bb34e1f"

inherit pypi setuptools3

SRC_URI += " \
    file://0001-remove-requirement-of-nose.patch \
"
