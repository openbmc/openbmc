SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=52d273a3054ced561275d4d15260ecda"

SRC_URI[sha256sum] = "dd505485549a7a552833da5e6063639d0d177c04f23bc3864e41e5dc5f612168"

inherit pypi python_hatchling

SRC_URI += " \
    file://CVE-2025-50181.patch \
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
