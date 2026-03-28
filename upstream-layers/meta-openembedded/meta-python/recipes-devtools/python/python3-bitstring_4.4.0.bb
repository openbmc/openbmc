SUMMARY = "Simple construction, analysis and modification of binary data."
HOMEPAGE = "https://github.com/scott-griffiths/bitstring"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=963a24c175e658fbf16a764135121ffa"

SRC_URI[sha256sum] = "e682ac522bb63e041d16cbc9d0ca86a4f00194db16d0847c7efe066f836b2e37"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "\
    python3-core \
    python3-io \
    python3-mmap \
    python3-numbers \
    python3-bitarray \
"

BBCLASSEXTEND = "native nativesdk"
