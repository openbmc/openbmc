SUMMARY = "Python wrapper for Linux fanotify."
HOMEPAGE = "https://github.com/baskiton/pyfanotify"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=636a36c9df04efcfacf839b8866d9a37"

SRC_URI += "file://0001-ext-define-FNM_EXTMATCH-if-not-already-defined.patch"
SRC_URI[sha256sum] = "fd62dccdf3c17ca117e3279f0cbc65c639e53c9dec8a459d44ed6a35c1a18e60"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-datetime \
    python3-logging \
    python3-multiprocessing \
"
