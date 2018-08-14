SUMMARY = "Pidfile featuring stale detection and file-locking, can also \
be used as context-manager or decorator"
HOMEPAGE = "https://github.com/trbs/pid/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=83d53cbd3105063f20305bc313464e29"

SRC_URI[md5sum] = "04d30308013d16aa882b3806feda8ab2"
SRC_URI[sha256sum] = "d8bb2ceec21a4ae84be6e9d320db1f56934b30e676e31c6f098ca7218b3d67d4"

inherit pypi setuptools3

SRC_URI += " \
    file://0001-remove-requirement-of-nose.patch \
"
