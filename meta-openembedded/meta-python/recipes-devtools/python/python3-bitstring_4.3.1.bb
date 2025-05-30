SUMMARY = "Simple construction, analysis and modification of binary data."
HOMEPAGE = "https://github.com/scott-griffiths/bitstring"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=963a24c175e658fbf16a764135121ffa"

SRC_URI[sha256sum] = "a08bc09d3857216d4c0f412a1611056f1cc2b64fd254fb1e8a0afba7cfa1a95a"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "\
    python3-core \
    python3-io \
    python3-mmap \
    python3-numbers \
    python3-bitarray \
"

BBCLASSEXTEND = "native nativesdk"
