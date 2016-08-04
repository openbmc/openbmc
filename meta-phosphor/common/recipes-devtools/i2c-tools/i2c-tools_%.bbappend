# Prefer the Yocto mirror over the direct lm-sensors download.
#echo BBAPPEND 1
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_remove = "http://dl.lm-sensors.org/i2c-tools/releases/${BP}.tar.bz2"
SRC_URI =+ "http://downloads.yoctoproject.org/mirror/sources/${BP}.tar.bz2 \		file://0001-4-byte-read-support-466.patch"

RDEPENDS_${PN}_remove = "${PN}-misc"
#echo BBAPPEND 2
