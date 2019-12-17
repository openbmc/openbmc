SUMMARY = "a2jmidid is daemon for exposing ALSA sequencer applications as JACK MIDI"
SECTION = "libs/multimedia"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=751419260aa954499f7abaabaa882bbe \
"

DEPENDS = "alsa-lib jack dbus"
DEPENDS_append_libc-musl = " libexecinfo"

SRCREV = "de37569c926c5886768f892c019e3f0468615038"
SRC_URI = " \
    git://github.com/linuxaudio/a2jmidid;protocol=https \
    file://riscv_ucontext.patch \
"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = "-Db_lto=false"

LDFLAGS_append_libc-musl = " -lexecinfo"

export LINKFLAGS="${LDFLAGS}"

FILES_${PN} += "${datadir}/dbus-1/services"
