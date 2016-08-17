SUMMARY = "PhysicsFS is a library to provide abstract access to various archives"
HOMEAPAGE = "http://icculus.org/physfs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5d94e3eaaa10b00ca803ba35a3e87cde"
DEPENDS = "readline zlib"

inherit cmake

SRC_URI = "http://icculus.org/${BPN}/downloads/${BP}.tar.bz2"
SRC_URI[md5sum] = "c2c727a8a8deb623b521b52d0080f613"
SRC_URI[sha256sum] = "ca862097c0fb451f2cacd286194d071289342c107b6fe69079c079883ff66b69"
