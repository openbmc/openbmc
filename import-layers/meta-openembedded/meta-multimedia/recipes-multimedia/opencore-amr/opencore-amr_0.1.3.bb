DESCRIPTION = "OpenCORE Adaptive Multi Rate (AMR) speech codec library implementation"
HOMEPAGE = "http://sourceforge.net/projects/opencore-amr/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=dd2c2486aca02190153cf399e508c7e7"

SRC_URI = "${SOURCEFORGE_MIRROR}/opencore-amr/${BP}.tar.gz"
SRC_URI[md5sum] = "09d2c5dfb43a9f6e9fec8b1ae678e725"
SRC_URI[sha256sum] = "106bf811c1f36444d7671d8fd2589f8b2e0cca58a2c764da62ffc4a070595385"

inherit autotools
