DESCRIPTION = "Unprivileged sandboxing tool"
HOMEPAGE = "https://github.com/containers/bubblewrap"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "libcap"

SRC_URI = "https://github.com/containers/${BPN}/releases/download/v${PV}/${BP}.tar.xz"
SRC_URI[sha256sum] = "8a0ec802d1b3e956c5bb0a40a81c9ce0b055a31bf30a8efa547433603b8af20b"

UPSTREAM_CHECK_URI = "https://github.com/containers/bubblewrap/releases"
UPSTREAM_CHECK_REGEX = "bubblewrap-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools bash-completion manpages pkgconfig

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[manpages] = "--enable-man,--disable-man,libxslt-native docbook-xsl-stylesheets-native xmlto-native"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"
PACKAGECONFIG[setuid] = "--with-priv-mode=setuid,--with-priv-mode=none"

PACKAGES += "${PN}-zsh-completion"

FILES:${PN}-zsh-completion = "${datadir}/zsh/site-functions"
