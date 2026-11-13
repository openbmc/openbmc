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

SRCREV = "1bca62311e447fd9c39fc93de2c4b96cd5ce971c"
SRC_URI = "git://github.com/cisco/openh264.git;protocol=https;branch=master;tag=v${PV} \
           "

COMPATIBLE_MACHINE:powerpc64le = "null"
COMPATIBLE_MACHINE:riscv32 = "null"
