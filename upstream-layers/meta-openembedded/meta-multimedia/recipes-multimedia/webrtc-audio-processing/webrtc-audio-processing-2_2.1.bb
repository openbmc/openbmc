DESCRIPTION = "Audio processing bits of the WebRTC reference implementation"
HOMEPAGE = "https://www.freedesktop.org/software/pulseaudio/webrtc-audio-processing/"
SECTION = "audio"

DEPENDS = "abseil-cpp"
DEPENDS:append:libc-musl = " libexecinfo"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=da08a38a32a340c5d91e13ee86a118f2"

SRC_URI = " \
    http://freedesktop.org/software/pulseaudio/webrtc-audio-processing/webrtc-audio-processing-${PV}.tar.xz \
    file://e9c78dc4712fa6362b0c839ad57b6b46dce1ba83.patch \
    file://60.patch \
"
SRC_URI[sha256sum] = "ae9302824b2038d394f10213cab05312c564a038434269f11dbf68f511f9f9fe"
S = "${UNPACKDIR}/webrtc-audio-processing-${PV}"

LDFLAGS:append:libc-musl = " -lexecinfo"
# | riscv32-yoe-linux-musl-ld.lld: error: undefined reference: __atomic_store_8
LDFLAGS:append:riscv32 = " -latomic"
inherit meson pkgconfig
