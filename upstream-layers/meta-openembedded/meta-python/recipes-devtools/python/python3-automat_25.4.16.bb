DESCRIPTION = "Self-service finite-state machines for the programmer on the go"
HOMEPAGE = "https://github.com/glyph/Automat"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ad213bcca81688e94593e5f60c87477"

SRC_URI[sha256sum] = "0017591a5477066e90d26b0e696ddc143baafd87b588cfac8100bc6be9634de0"

inherit pypi python_hatchling

DEPENDS += "\
    python3-hatch-vcs-native \
"

