SRC_URI_remove = "http://dl.lm-sensors.org/i2c-tools/releases/${BP}.tar.bz2"
SRC_URI_prepend = "http://downloads.yoctoproject.org/mirror/sources/i2c-tools-3.1.2.tar.bz2"

RDEPENDS_${PN}_remove = "${PN}-misc"
