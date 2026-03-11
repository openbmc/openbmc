SUMMARY = "genimage - The image creation tool"
DESCRIPTION = "Genimage is a tool for generating filesystem and disk/flash images \
from a root filesystem tree or existing filesystem images."
HOMEPAGE = "https://github.com/pengutronix/genimage"

SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=570a9b3749dd0463a1778803b12a6dce"

DEPENDS = "libconfuse"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/genimage-${PV}.tar.xz"
SRC_URI[sha256sum] = "7ec4fcb865662a8b2ff20284819044ffa84137bf3ca16fb749701291bc01f108"

EXTRA_OECONF = "--enable-largefile"

inherit pkgconfig autotools gettext github-releases

GITHUB_BASE_URI = "https://github.com/pengutronix/genimage/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)*)"

BBCLASSEXTEND = "native nativesdk"
