SUMMARY = "Library for reading mod-like audio files"
HOMEPAGE = "http://modplug-xmms.sf.net"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9182faa1f7c316f7b97d404bcbe3685"

SRC_URI = "${SOURCEFORGE_MIRROR}/modplug-xmms/libmodplug-${PV}.tar.gz"
SRC_URI[md5sum] = "5f30241db109d647781b784e62ddfaa1"
SRC_URI[sha256sum] = "77462d12ee99476c8645cb5511363e3906b88b33a6b54362b4dbc0f39aa2daad"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-option-checking"

# NOTE: autotools_stage_all does nothing here, we need to do it manually
do_install_append() {
    install -d ${D}${includedir}/libmodplug
    install -m 0644 ${S}/src/modplug.h ${D}${includedir}/libmodplug
    install -m 0644 ${S}/src/modplug.h ${D}${includedir}/
}

