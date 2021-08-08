DESCRIPTION = "OpenH264 is a codec library which supports H.264 encoding and \
decoding. It is suitable for use in real time applications such as WebRTC."
HOMEPAGE = "http://www.openh264.org/"
SECTION = "libs/multimedia"

DEPENDS:append:x86 = " nasm-native"
DEPENDS:append:x86-64 = " nasm-native"

LICENSE = "BSD-2-Clause"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb6d3771da6a07d33fd50d4d9aa73bcf"

S = "${WORKDIR}/git"
SRCREV = "50a1fcf70fafe962c526749991cb4646406933ba"
BRANCH = "openh264v2.1.1"
SRC_URI = "git://github.com/cisco/openh264.git;protocol=https;branch=${BRANCH} \
           file://0001-Makefile-Use-cp-options-to-preserve-file-mode.patch \
           "

COMPATIBLE_MACHINE:armv7a = "(.*)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"
COMPATIBLE_MACHINE:mips = "(.*)"
COMPATIBLE_MACHINE:mips64 = "(.*)"
COMPATIBLE_MACHINE:powerpc64le = "null"

EXTRA_OEMAKE:armv7a = "ARCH=arm"
EXTRA_OEMAKE:armv7ve = "ARCH=arm"
EXTRA_OEMAKE:aarch64 = "ARCH=arm64"
EXTRA_OEMAKE:x86 = "ARCH=i386"
EXTRA_OEMAKE:x86-64 = "ARCH=x86_64"
EXTRA_OEMAKE:mips = "ARCH=mips"
EXTRA_OEMAKE:mips64 = "ARCH=mips64"
EXTRA_OEMAKE:riscv64 = "ARCH=riscv64"

EXTRA_OEMAKE:append = " ENABLEPIC=Yes"
do_configure() {
    :
}

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install DESTDIR=${D} PREFIX=${prefix}
}

CLEANBROKEN = "1"
