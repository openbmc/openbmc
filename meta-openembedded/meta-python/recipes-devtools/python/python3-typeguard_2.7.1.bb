SUMMARY = "Run-time type checker for Python"
HOMEPAGE = "https://pypi.org/project/typeguard/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0e423eea5c91e7aa21bdb70184b3e53"

SRC_URI[md5sum] = "ef743359de59f8fe17e7c5e3af70e2c5"
SRC_URI[sha256sum] = "2d545c71e9439c21bcd7c28f5f55b3606e6106f7031ab58375656a1aed483ef2"

inherit pypi setuptools3

DEPENDS += "\
    python3-distutils-extra-native \
    python3-setuptools-scm-native \
"

RDEPENDS_${PN} += "python3-typing"

BBCLASSEXTEND = "native nativesdk"
