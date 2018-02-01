SUMMARY = "a2jmidid is daemon for exposing ALSA sequencer applications as JACK MIDI"
SECTION = "libs/multimedia"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = " \
    file://gpl2.txt;md5=751419260aa954499f7abaabaa882bbe \
"

DEPENDS = "alsa-lib jack dbus"
DEPENDS_append_libc-musl = " libexecinfo"

SRC_URI = " \
    http://download.gna.org/${BPN}/${BPN}-${PV}.tar.bz2 \
    file://0001-wscript-add-pthread-library-dependency-to-fix-linkin.patch \
    file://0002-aarch64.patch \
"
SRC_URI[md5sum] = "9cf4edbc3ad2ddeeaf6c8c1791ff3ddd"
SRC_URI[sha256sum] = "2a9635f62aabc59edb54ada07048dd47e896b90caff94bcee710d3582606f55f"

inherit waf pkgconfig

LDFLAGS_append_libc-musl = " -lexecinfo"

export LINKFLAGS="${LDFLAGS}"

FILES_${PN} += "${datadir}/dbus-1/services"
