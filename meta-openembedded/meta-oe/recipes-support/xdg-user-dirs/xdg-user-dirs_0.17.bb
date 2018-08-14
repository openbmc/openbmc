DESCRIPTION = "xdg-user-dirs is a tool to help manage user directories like the desktop folder and the music folder"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://user-dirs.freedesktop.org/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "e0564ec6d838e6e41864d872a29b3575"
SRC_URI[sha256sum] = "2a07052823788e8614925c5a19ef5b968d8db734fdee656699ea4f97d132418c"

inherit autotools gettext

EXTRA_OECONF = "--disable-documentation"

CONFFILES_${PN} += " \
    ${sysconfdir}/xdg/user-dirs.conf \
    ${sysconfdir}/xdg/user-dirs.defaults \
"
