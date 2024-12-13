SUMMARY = "genimage - The image creation tool"
DESCRIPTION = "Genimage is a tool for generating filesystem and disk/flash images \
from a root filesystem tree or existing filesystem images."
HOMEPAGE = "https://github.com/pengutronix/genimage"

SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libconfuse"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/genimage-${PV}.tar.xz"
SRC_URI[sha256sum] = "ebc3f886c4d80064dd6c6d5e3c2e98e5a670078264108ce2f89ada8a2e13fedd"

EXTRA_OECONF = "--enable-largefile"

inherit pkgconfig autotools gettext github-releases

GITHUB_BASE_URI = "https://github.com/pengutronix/genimage/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)*)"

BBCLASSEXTEND = "native nativesdk"
