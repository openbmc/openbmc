DESCRIPTION = "Minimalistic C client library for Redis"
HOMEPAGE = "http://github.com/redis/hiredis"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d84d659a35c666d23233e54503aaea51"
DEPENDS = "redis"

SRC_URI = "git://github.com/redis/hiredis;protocol=https;branch=master"
SRCREV = "b731283245f3183af527237166261ad0768ba7d4"

S = "${WORKDIR}/git"

inherit cmake
