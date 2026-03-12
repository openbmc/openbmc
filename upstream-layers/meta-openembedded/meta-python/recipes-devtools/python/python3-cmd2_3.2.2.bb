SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "https://github.com/python-cmd2/cmd2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f2a861642858e0858af9dd4e4716bae0"

DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "602a10c7ef79eae2b7c8b3874b8ac3a32d9278ceb90c997a2eeb9fc0041ab733"

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
