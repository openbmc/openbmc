SUMMARY = "Open Source H.264 Codec"
DESCRIPTION = "OpenH264 is a codec library which supports H.264 encoding and \
decoding. It is suitable for use in real time applications such as WebRTC."
HOMEPAGE = "http://www.openh264.org/"
SECTION = "libs/multimedia"
LICENSE = "BSD-2-Clause"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb6d3771da6a07d33fd50d4d9aa73bcf"

DEPENDS = " nasm-native"

inherit meson pkgconfig

S = "${WORKDIR}/git"
SRCREV = "19120fcb8f89b16126e9cfee096b2f0751554fdc"
BRANCH = "openh264v${PV}"
SRC_URI = "git://github.com/cisco/openh264.git;protocol=https;branch=${BRANCH} \
           "

COMPATIBLE_MACHINE:powerpc64le = "null"
COMPATIBLE_MACHINE:riscv32 = "null"
