SUMMARY = "Python wrapper for Linux fanotify."
HOMEPAGE = "https://github.com/baskiton/pyfanotify"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=636a36c9df04efcfacf839b8866d9a37"

SRC_URI += "file://0001-ext-define-FNM_EXTMATCH-if-not-already-defined.patch"
SRC_URI[sha256sum] = "90219aa9f8b78fa732f24aa7b21c7bb6ac97a6eb47f1763c899b8194e23af1df"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-datetime \
    python3-logging \
    python3-multiprocessing \
"
