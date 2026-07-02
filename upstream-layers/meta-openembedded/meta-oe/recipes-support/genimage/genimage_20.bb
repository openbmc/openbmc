SUMMARY = "genimage - The image creation tool"
DESCRIPTION = "Genimage is a tool for generating filesystem and disk/flash images \
from a root filesystem tree or existing filesystem images."
HOMEPAGE = "https://github.com/pengutronix/genimage"

SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=570a9b3749dd0463a1778803b12a6dce"

DEPENDS = "libconfuse"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/genimage-${PV}.tar.xz"
SRC_URI[sha256sum] = "397545be2fa52d482c5fba9a11897c61324bfc559ea0efb700b9edba7a807b55"

# When cross compiling, configure cannot probe for a shell that supports
# "set -o pipefail", so it must be specified explicitly. genimage uses this
# shell at runtime to run command pipelines, so point it at bash.
EXTRA_OECONF = "--enable-largefile --with-shell=/bin/bash"

inherit pkgconfig autotools gettext github-releases

RDEPENDS:${PN}:append:class-target = " bash"

GITHUB_BASE_URI = "https://github.com/pengutronix/genimage/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)*)"

BBCLASSEXTEND = "native nativesdk"
