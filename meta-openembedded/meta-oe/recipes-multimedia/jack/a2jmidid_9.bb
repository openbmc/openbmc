SUMMARY = "a2jmidid is daemon for exposing ALSA sequencer applications as JACK MIDI"
SECTION = "libs/multimedia"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=751419260aa954499f7abaabaa882bbe \
"

DEPENDS = "alsa-lib jack dbus"
DEPENDS:append:libc-musl = " libexecinfo"

SRCREV = "de37569c926c5886768f892c019e3f0468615038"
SRC_URI = " \
    git://github.com/linuxaudio/a2jmidid;protocol=https;branch=master \
    file://riscv_ucontext.patch \
    file://ppc_musl_ucontext.patch \
"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = "-Db_lto=false"

LDFLAGS:append:libc-musl = " -lexecinfo"

export LINKFLAGS="${LDFLAGS}"

FILES:${PN} += "${datadir}/dbus-1/services"
