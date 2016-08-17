SUMMARY  = "Tornado-Redis is an asyncronous Redis client for the Tornado Web Server"
DESCRIPTION = "Tornado-Redis is a Redis client that uses Tornado's native 'tornado-gen' interface. \
It can be used alongside redis-py in Tornado applications: \
tornado-redis to subscribe to Pub/Sub notifications and for blocking commands (such as BLPOP, BRPOP, BRPOPLPUSH); \
redis-py for other commands."
HOMEPAGE = "https://github.com/leporo/tornado-redis"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=ca307e8f37b5fa7f8dbbec033f7db7de"

SRC_URI[md5sum] = "1c7ec0f645d15400871141c1149e6934"
SRC_URI[sha256sum] = "8fd3b324158291ad5fb7f5f8dc2e8763b2895556bd2a44f2dd721b703c669046"

inherit pypi setuptools

RDEPENDS_${PN} += "python-tornado"

RDEPENDS_${PN}-test += "${PN} python-tornado"
