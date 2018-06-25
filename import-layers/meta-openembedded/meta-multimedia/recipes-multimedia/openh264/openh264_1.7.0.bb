DESCRIPTION = "OpenH264 is a codec library which supports H.264 encoding and \
decoding. It is suitable for use in real time applications such as WebRTC."
HOMEPAGE = "http://www.openh264.org/"
SECTION = "libs/multimedia"

DEPENDS_x86 += "nasm-native"
DEPENDS_x86-64 += "nasm-native"

LICENSE = "BSD-2-Clause"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb6d3771da6a07d33fd50d4d9aa73bcf"

S = "${WORKDIR}/git"
SRCREV = "a180c9d4d6f1a4830ca9eed9d159d54996bd63cb"
BRANCH = "openh264v1.7"
SRC_URI = "git://github.com/cisco/openh264.git;protocol=https;branch=${BRANCH};"

COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_mips = "(.*)"
COMPATIBLE_MACHINE_mips64 = "(.*)"

EXTRA_OEMAKE_armv7a = "ARCH=arm"
EXTRA_OEMAKE_aarch64 = "ARCH=arm64"
EXTRA_OEMAKE_x86 = "ARCH=i386"
EXTRA_OEMAKE_x86-64 = "ARCH=x86_64"
EXTRA_OEMAKE_mips = "ARCH=mips"
EXTRA_OEMAKE_mips64 = "ARCH=mips64"

do_configure() {
    :
}

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install DESTDIR=${D} PREFIX=${prefix}
}
