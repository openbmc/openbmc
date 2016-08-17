SUMMARY = "some essentials for string handling (and a bit more)"
HOMEPAGE = "http://libestr.adiscon.com/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=9d6c993486c18262afba4ca5bcb894d0"

SRC_URI = "http://libestr.adiscon.com/files/download/${BP}.tar.gz"

SRC_URI[md5sum] = "f4c9165a23587e77f7efe65d676d5e8e"
SRC_URI[sha256sum] = "bd655e126e750edd18544b88eb1568d200a424a0c23f665eb14bbece07ac703c"

inherit autotools
