DESCRIPTION = "libavc1394 is a programming interface for the 1394 Trade \
Association AV/C (Audio/Video Control) Digital Interface Command Set"
HOMEPAGE = "http://sourceforge.net/projects/libavc1394/"
SECTION = "libs/multimedia"

DEPENDS = "libraw1394"
DEPENDS_append_libc-musl = " argp-standalone"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=dcf3c825659e82539645da41a7908589"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "caf0db059d8b8d35d6f08e6c0e1c7dfe"
SRC_URI[sha256sum] = "7cb1ff09506ae911ca9860bef4af08c2403f3e131f6c913a2cbd6ddca4215b53"

inherit autotools pkgconfig

LDFLAGS_append_libc-musl = " -largp"
