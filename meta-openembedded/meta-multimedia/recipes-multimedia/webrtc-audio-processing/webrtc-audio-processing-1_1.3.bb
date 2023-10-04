DESCRIPTION = "Audio processing bits of the WebRTC reference implementation"
HOMEPAGE = "https://www.freedesktop.org/software/pulseaudio/webrtc-audio-processing/"
SECTION = "audio"

DEPENDS = "abseil-cpp"
DEPENDS:append:libc-musl = " libexecinfo"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=da08a38a32a340c5d91e13ee86a118f2"

SRC_URI = " \
    http://freedesktop.org/software/pulseaudio/webrtc-audio-processing/webrtc-audio-processing-${PV}.tar.xz \
    file://0001-add-missing-header-for-musl.patch \
    file://0001-Fix-return-type-errors.patch \
"
SRC_URI[sha256sum] = "2365e93e778d7b61b5d6e02d21c47d97222e9c7deff9e1d0838ad6ec2e86f1b9"
S = "${WORKDIR}/webrtc-audio-processing-${PV}"

LDFLAGS:append:libc-musl = " -lexecinfo"

inherit meson pkgconfig
