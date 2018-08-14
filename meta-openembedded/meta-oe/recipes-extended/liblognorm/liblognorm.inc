SUMMARY = "Fast samples-based log normalization library"
DESCRIPTION = "Briefly described, liblognorm is a tool to normalize log data."

HOMEPAGE = "http://www.liblognorm.com"
SECTION = "base"

LICENSE = "LGPL-2.1+"

SRC_URI = "http://www.liblognorm.com/files/download/${BPN}-${PV}.tar.gz\
"

inherit autotools pkgconfig

DEPENDS += "libee libestr json-c"

PACKAGECONFIG[debug] = "--enable-debug,--disable-debug,,"
PACKAGECONFIG[docs] = "--enable-docs,--disable-docs,sphinx,"
