DESCRIPTION = "library for easy implementation of a RDP/VNC server"
HOMEPAGE = "https://libvnc.github.io"
SECTION = "libs"
PRIORITY = "optional"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f"

DEPENDS += "zlib libsdl jpeg libpng gtk+ libgcrypt nettle gnutls gmp"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"
PACKAGECONFIG[systemd] = ",,systemd"

RDEPENDS_${PN} += "libpng gtk+ libgcrypt"

inherit distro_features_check autotools binconfig pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://github.com/LibVNC/libvncserver/archive/LibVNCServer-${PV}.tar.gz"
SRC_URI[md5sum] = "7f06104d5c009813e95142932c4ddb06"
SRC_URI[sha256sum] = "193d630372722a532136fd25c5326b2ca1a636cbb8bf9bb115ef869c804d2894"

S = "${WORKDIR}/${BPN}-LibVNCServer-${PV}"

TARGET_LDFLAGS += "-lgcrypt"
