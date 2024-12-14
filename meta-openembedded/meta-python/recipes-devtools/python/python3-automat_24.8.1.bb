DESCRIPTION = "Self-service finite-state machines for the programmer on the go"
HOMEPAGE = "https://github.com/glyph/Automat"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ad213bcca81688e94593e5f60c87477"

SRC_URI[sha256sum] = "b34227cf63f6325b8ad2399ede780675083e439b20c323d376373d8ee6306d88"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

