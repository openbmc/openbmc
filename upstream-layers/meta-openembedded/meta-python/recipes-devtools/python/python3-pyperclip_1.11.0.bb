DESCRIPTION = "A cross-platform clipboard module for Python. (only handles plain text for now)"
HOMEPAGE = "https://github.com/asweigart/pyperclip"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b6cd06fd72984ac1f8428337aec8cff7"

SRC_URI[sha256sum] = "244035963e4428530d9e3a6101a1ef97209c6825edab1567beac148ccc1db1b6"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-ctypes \
"

BBCLASSEXTEND = "native nativesdk"
