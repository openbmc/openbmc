SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/urllib3/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=52d273a3054ced561275d4d15260ecda"

SRC_URI[sha256sum] = "f8c5449b3cf0861679ce7e0503c7b44b5ec981bec0d1d3795a07f1ba96f0204d"

inherit pypi python_hatchling

DEPENDS += " \
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += "\
    python3-certifi \
    python3-cryptography \
    python3-email \
    python3-idna \
    python3-json \
    python3-netclient \
    python3-pyopenssl \
    python3-threading \
    python3-logging \
"

CVE_PRODUCT = "urllib3"

BBCLASSEXTEND = "native nativesdk"
