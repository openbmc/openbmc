DESCRIPTION = "VisualOn Advanced Audio Coding (AAC) encoder"
HOMEPAGE = "http://sourceforge.net/projects/opencore-amr/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=dd2c2486aca02190153cf399e508c7e7"

SRC_URI = "${SOURCEFORGE_MIRROR}/opencore-amr/${BP}.tar.gz"
SRC_URI[md5sum] = "b574da1d92d75fc40b0b75aa16f24ac4"
SRC_URI[sha256sum] = "e51a7477a359f18df7c4f82d195dab4e14e7414cbd48cf79cc195fc446850f36"

inherit autotools
