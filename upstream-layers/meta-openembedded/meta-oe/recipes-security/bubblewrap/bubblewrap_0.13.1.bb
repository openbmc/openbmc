DESCRIPTION = "Unprivileged sandboxing tool"
HOMEPAGE = "https://github.com/containers/bubblewrap"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libcap"

SRC_URI = " \
    https://github.com/containers/${BPN}/releases/download/v${PV}/${BP}.tar.xz \
"
SRC_URI[sha256sum] = "0e85c8702248b8dd64144f594147553650cdd0e4c951690345faace79c217c4d"

inherit meson bash-completion github-releases manpages pkgconfig

GITHUB_BASE_URI = "https://github.com/containers/${BPN}/releases/"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[manpages] = "-Dman=enabled,-Dman=disabled,libxslt-native docbook-xsl-stylesheets-native xmlto-native"
PACKAGECONFIG[selinux] = "-Dselinux=enabled,-Dselinux=disabled,libselinux"

PACKAGES += "${PN}-zsh-completion"

FILES:${PN}-zsh-completion = "${datadir}/zsh/site-functions"

BBCLASSEXTEND = "native"
