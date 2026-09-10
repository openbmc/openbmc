SUMMARY = "An open source MPEG-4 and MPEG-2 AAC decoding library"
HOMEPAGE = "http://www.audiocoding.com/faad2.html"
SECTION = "libs"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=381c8cbe277a7bc1ee2ae6083a04c958"

LICENSE_FLAGS = "commercial"

SRC_URI = "git://github.com/knik0/faad2.git;branch=master;protocol=https;tag=${PV}"
SRCREV = "6918ebb51b8f7e86278da15884bd7114e4b9661e"


inherit cmake
