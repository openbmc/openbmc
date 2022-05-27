SUMMARY = "Python wrapper for Linux fanotify."
HOMEPAGE = "https://github.com/baskiton/pyfanotify"
AUTHOR = "Alexander Baskikh"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=636a36c9df04efcfacf839b8866d9a37"

SRC_URI += "file://0001-ext-define-FNM_EXTMATCH-if-not-already-defined.patch"
SRC_URI[sha256sum] = "0efa73922fd705b4e8f8f0b51cb88198ceef66cc309e1de21674ef44c879029d"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-datetime \
    python3-logging \
    python3-multiprocessing \
"
