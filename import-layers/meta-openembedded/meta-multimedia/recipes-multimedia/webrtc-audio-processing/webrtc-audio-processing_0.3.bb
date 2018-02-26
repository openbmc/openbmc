DESCRIPTION = "Audio processing bits of the WebRTC reference implementation"
HOMEPAGE = "https://www.freedesktop.org/software/pulseaudio/webrtc-audio-processing/"
SECTION = "audio"

DEPENDS_append_libc-musl = " libexecinfo"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=da08a38a32a340c5d91e13ee86a118f2 \
                    file://webrtc/common.h;beginline=1;endline=9;md5=41f7322d91deabaf0acbbd0b8d0bc548 \
"

# Note that patch 3 effectively reverts patches 1 and 2. The only reason
# why patches 1 and 2 are included is that otherwise patch 3 wouldn't
# apply cleanly.
SRC_URI = "http://freedesktop.org/software/pulseaudio/webrtc-audio-processing/${BP}.tar.xz \
           file://0001-build-Protect-against-unsupported-CPU-types.patch \
           file://0002-build-Add-ARM-64bit-support.patch \
           file://0003-build-fix-architecture-detection.patch \
           file://0004-typedefs.h-add-support-for-64-bit-and-big-endian-MIP.patch \
           file://0005-typedefs.h-add-support-for-PowerPC.patch \
           file://0006-common_audio-implement-endianness-conversion-in-wav-.patch \
"

SRC_URI[md5sum] = "336ae032f608e65808ac577cde0ab72c"
SRC_URI[sha256sum] = "756e291d4f557d88cd50c4fe3b8454ec238362d22cedb3e6173240d90f0a80fa"

LDFLAGS_append_libc-musl = " -lexecinfo"
inherit autotools
