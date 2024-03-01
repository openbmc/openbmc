SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "https://github.com/python-cmd2/cmd2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fad7740aa21780c8b9a214f5b320b4ad"

DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "71873c11f72bd19e2b1db578214716f0d4f7c8fa250093c601265a9a717dee52"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-attrs \
    python3-colorama \
    python3-pyperclip \
    python3-wcwidth \
    python3-compression \
    python3-pydoc \
    python3-json \
    python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
