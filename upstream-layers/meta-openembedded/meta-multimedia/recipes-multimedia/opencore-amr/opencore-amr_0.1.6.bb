DESCRIPTION = "OpenCORE Adaptive Multi Rate (AMR) speech codec library implementation"
HOMEPAGE = "http://sourceforge.net/projects/opencore-amr/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dd2c2486aca02190153cf399e508c7e7"

SRC_URI = "${SOURCEFORGE_MIRROR}/opencore-amr/${BP}.tar.gz"
SRC_URI[md5sum] = "03de025060a4f16c4c44218f65e13e95"
SRC_URI[sha256sum] = "483eb4061088e2b34b358e47540b5d495a96cd468e361050fae615b1809dc4a1"

inherit autotools
