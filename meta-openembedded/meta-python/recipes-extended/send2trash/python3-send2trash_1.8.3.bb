SUMMARY = "Send file to trash natively under Mac OS X, Windows and Linux"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=05faa35ba1ca10b723f19d286c9d5237"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "b18e7a3966d99871aefeb00cfbcfdced55ce4871194810fc71f4aa484b953abf"

PYPI_PACKAGE = "Send2Trash"

RDEPENDS:${PN} += "\
    python3-io \
    python3-datetime \
"
