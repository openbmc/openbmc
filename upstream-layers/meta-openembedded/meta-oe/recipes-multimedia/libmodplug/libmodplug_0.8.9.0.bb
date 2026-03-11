SUMMARY = "Library for reading mod-like audio files"
HOMEPAGE = "http://modplug-xmms.sf.net"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9182faa1f7c316f7b97d404bcbe3685"

SRC_URI = "${SOURCEFORGE_MIRROR}/modplug-xmms/libmodplug-${PV}.tar.gz \
           file://0001-fastmix-Drop-register-storage-class-keyword.patch"

SRC_URI[sha256sum] = "457ca5a6c179656d66c01505c0d95fafaead4329b9dbaa0f997d00a3508ad9de"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-option-checking"

# NOTE: autotools_stage_all does nothing here, we need to do it manually
do_install:append() {
    install -d ${D}${includedir}/libmodplug
    install -m 0644 ${S}/src/modplug.h ${D}${includedir}/libmodplug
    install -m 0644 ${S}/src/modplug.h ${D}${includedir}/
}
