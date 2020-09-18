SUMMARY = "An alternate posix capabilities library"
DESCRIPTION = "The libcap-ng library is intended to make programming \
with POSIX capabilities much easier than the traditional libcap library."
HOMEPAGE = "http://freecode.com/projects/libcap-ng"
SECTION = "base"
LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
		    file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

SRC_URI = "http://people.redhat.com/sgrubb/libcap-ng/libcap-ng-${PV}.tar.gz \
           file://python.patch \
"

SRC_URI[md5sum] = "5883ed10b621c87e29a05cff36d2928e"
SRC_URI[sha256sum] = "85815c711862d01a440db471f12fba462c9949e923966f5859607e652d9c0ae9"

BBCLASSEXTEND = "native nativesdk"
