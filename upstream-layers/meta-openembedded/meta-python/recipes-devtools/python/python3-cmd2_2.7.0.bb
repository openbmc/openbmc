SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "https://github.com/python-cmd2/cmd2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=26efe44f9946f43292f90070d9d5590c"

DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "81d8135b46210e1d03a5a810baf859069a62214788ceeec3588f44eed86fbeeb"

inherit pypi python_setuptools_build_meta python3native

RDEPENDS:${PN} += "\
    python3-attrs \
    python3-colorama \
    python3-pyperclip \
    python3-wcwidth \
    python3-compression \
    python3-pydoc \
    python3-json \
    python3-numbers \
    python3-rich-argparse \
"

BBCLASSEXTEND = "native nativesdk"
