DESCRIPTION = "Unprivileged sandboxing tool"
HOMEPAGE = "https://github.com/containers/bubblewrap"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "libcap"

SRC_URI = " \
    https://github.com/containers/${BPN}/releases/download/v${PV}/${BP}.tar.xz \
"
SRC_URI[sha256sum] = "988fd6b232dafa04b8b8198723efeaccdb3c6aa9c1c7936219d5791a8b7a8646"

inherit meson bash-completion github-releases manpages pkgconfig

GITHUB_BASE_URI = "https://github.com/containers/${BPN}/releases/"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[manpages] = "-Dman=enabled,-Dman=disabled,libxslt-native docbook-xsl-stylesheets-native xmlto-native"
PACKAGECONFIG[selinux] = "-Dselinux=enabled,-Dselinux=disabled,libselinux"

PACKAGES += "${PN}-zsh-completion"

FILES:${PN}-zsh-completion = "${datadir}/zsh/site-functions"

BBCLASSEXTEND = "native"
