SUMMARY = "Recipe to embedded the Python PiP Package checksec_py"
HOMEPAGE = "https://pypi.org/project/checksec_py"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

PR = "r0"

inherit pypi python_poetry_core
PYPI_PACKAGE = "checksec_py"
SRC_URI[sha256sum] = "892854f95d17a76d8f45a5c0cc597b9f1bebced3fffb9c7205d0baaf5eace886"

SRC_URI += " \
    file://0001-main-Add-option-to-ignore-symlinks.patch \
"

RDEPENDS:${PN} += " \
    python3-docopt \
    python3-lief \
    python3-pylddwrap \
    python3-rich \
"

# python3-lief is not available for x86:
# https://github.com/lief-project/LIEF/commit/3def579f75965aa19c021d840a759bce2afc0a31#r152197203
COMPATIBLE_HOST:x86 = "null"

BBCLASSEXTEND = "native nativesdk"
