SUMMARY = "Telepathy IRC connection manager"
DESCRIPTION = "Telepathy implementation of the Internet Relay Chat protocols."
HOMEPAGE = "http://telepathy.freedesktop.org/wiki/"
DEPENDS = "glib-2.0 dbus telepathy-glib openssl libxslt-native"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/idle.c;beginline=1;endline=19;md5=b06b1e2594423111a1a7910b0eefc7f9"

SRC_URI = "http://telepathy.freedesktop.org/releases/${BPN}/${BPN}-${PV}.tar.gz \
           file://fix-svc-gtk-doc.h-target.patch"
SRC_URI[md5sum] = "92a2de5198284cbd3c430b0d1a971a86"
SRC_URI[sha256sum] = "3013ad4b38d14ee630b8cc8ada5e95ccaa849b9a6fe15d2eaf6d0717d76f2fab"

inherit autotools pkgconfig ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "pythonnative", "", d)}

FILES_${PN} += "${datadir}/telepathy \
                ${datadir}/dbus-1"

python() {
    if 'meta-python2' not in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('Requires meta-python2 to be present.')
}
