SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/urllib3/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=52d273a3054ced561275d4d15260ecda"

SRC_URI[sha256sum] = "231e0ec3b63ceb14667c67be60f2f2c40a518cb38b03af60abc813da26505f4c"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

PACKAGECONFIG ??= ""
# This is not recommended for use upstream, and has large dependencies
PACKAGECONFIG[openssl] = ",,,python3-cryptography python3-pyopenssl"

do_install:append() {
    if ! ${@bb.utils.contains("PACKAGECONFIG", "openssl", "true", "false", d)}; then
        rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/urllib3/contrib/pyopenssl.py
    fi
}

RDEPENDS:${PN} += "\
    python3-idna \
    python3-email \
    python3-json \
    python3-netclient \
    python3-threading \
    python3-logging \
"

CVE_PRODUCT = "urllib3"

BBCLASSEXTEND = "native nativesdk"
