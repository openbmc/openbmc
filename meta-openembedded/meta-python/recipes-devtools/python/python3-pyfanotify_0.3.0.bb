SUMMARY = "Python wrapper for Linux fanotify."
HOMEPAGE = "https://github.com/baskiton/pyfanotify"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=636a36c9df04efcfacf839b8866d9a37"

SRC_URI += "file://0001-ext-define-FNM_EXTMATCH-if-not-already-defined.patch"
SRC_URI[sha256sum] = "95ee17caec25436e10d59d5d45e28d2dc659819cc6de55f29fcbdcd5ee2fa8d3"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-datetime \
    python3-logging \
    python3-multiprocessing \
"
