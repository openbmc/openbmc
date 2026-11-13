SUMMARY = "ICMP ping in pure Python"
HOMEPAGE = "https://github.com/kyan001/ping3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5b1b7571ea8acab651bab228e3ec2907"

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "72588d79400d65dcd39c6c88a8f54b10ec46a9a0ff0a3e167d967e7e9c4fbe02"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-io \
    python3-logging \
"
