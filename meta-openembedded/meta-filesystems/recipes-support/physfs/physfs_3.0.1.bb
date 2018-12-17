SUMMARY = "PhysicsFS is a library to provide abstract access to various archives"
HOMEAPAGE = "http://icculus.org/physfs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2668e2fb051c3e564198e146a9a2d9f0"
DEPENDS = "readline zlib"

inherit cmake

PE = "1"

SRC_URI = "http://icculus.org/${BPN}/downloads/${BP}.tar.bz2"
SRC_URI[md5sum] = "359f102bcbd62accf84ef32f4863255d"
SRC_URI[sha256sum] = "b77b9f853168d9636a44f75fca372b363106f52d789d18a2f776397bf117f2f1"
