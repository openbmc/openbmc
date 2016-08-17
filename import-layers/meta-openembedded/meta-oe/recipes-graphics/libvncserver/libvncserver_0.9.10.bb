DESCRIPTION = "library for easy implementation of a RDP/VNC server"
HOMEPAGE = "https://libvnc.github.io"
SECTION = "libs"
PRIORITY = "optional"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f"

DEPENDS += "zlib libsdl jpeg libpng gtk+ libgcrypt nettle gnutls gmp"
RDEPENDS_${PN} += "libpng gtk+ libgcrypt"

inherit autotools binconfig pkgconfig

SRC_URI  = "\
	 ${DEBIAN_MIRROR}/main/libv/libvncserver/libvncserver_0.9.10+dfsg.orig.tar.xz \
	 file://0001-remove-webclients-build.patch \
	 file://0002-common-add-sha1.patch \
"

SRC_URI[md5sum] = "e883b6c7bd339a5e1c48645051abe5c4"
SRC_URI[sha256sum] = "583f28869b82aec57768d7d18cd7ff81bf092ecbbc1209b587c2c2cd68330250"

S = "${WORKDIR}/${BPN}-LibVNCServer-${PV}"

TARGET_LDFLAGS += "-lgcrypt"
