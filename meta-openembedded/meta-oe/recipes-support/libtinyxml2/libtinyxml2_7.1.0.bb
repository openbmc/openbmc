SUMMARY = "TinyXML-2 is a simple, small, efficient, C++ XML parser that can be easily integrating into other programs"
HOMEPAGE = "http://www.grinninglizard.com/tinyxml2/"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=135624eef03e1f1101b9ba9ac9b5fffd"

SRC_URI = "git://github.com/leethomason/tinyxml2.git"

SRCREV = "9c740e8d2341bd46dbe8e87053cdb4d931971967"

S = "${WORKDIR}/git"

inherit cmake

BBCLASSEXTEND = "native"
