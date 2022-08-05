SUMMARY = "Python wrapper for Linux fanotify."
HOMEPAGE = "https://github.com/baskiton/pyfanotify"
AUTHOR = "Alexander Baskikh"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=636a36c9df04efcfacf839b8866d9a37"

SRC_URI += "file://0001-ext-define-FNM_EXTMATCH-if-not-already-defined.patch"
SRC_URI[sha256sum] = "1ec1c61fba9dea96cf8eac7f1a0cca2517613da20d156b7da2a06f9d63c77aca"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-datetime \
    python3-logging \
    python3-multiprocessing \
"
