DESCRIPTION = "Audio processing bits of the WebRTC reference implementation"
HOMEPAGE = "https://www.freedesktop.org/software/pulseaudio/webrtc-audio-processing/"
SECTION = "audio"

DEPENDS:append:libc-musl = " libexecinfo"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=da08a38a32a340c5d91e13ee86a118f2 \
                    file://webrtc/common.h;beginline=1;endline=9;md5=41f7322d91deabaf0acbbd0b8d0bc548 \
"

SRC_URI = "http://freedesktop.org/software/pulseaudio/webrtc-audio-processing/${BP}.tar.xz \
           file://0004-typedefs.h-add-support-for-64-bit-and-big-endian-MIP.patch \
           file://0005-typedefs.h-add-support-for-PowerPC.patch \
           file://0006-common_audio-implement-endianness-conversion-in-wav-.patch \
           file://riscv_support.patch \
"

SRC_URI[md5sum] = "6e10724ca34bcbc715a4c208273acb0c"
SRC_URI[sha256sum] = "a0fdd938fd85272d67e81572c5a4d9e200a0c104753cb3c209ded175ce3c5dbf"

LDFLAGS:append:libc-musl = " -lexecinfo"
inherit autotools pkgconfig
