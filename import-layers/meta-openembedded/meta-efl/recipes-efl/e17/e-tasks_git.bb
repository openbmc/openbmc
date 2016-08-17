SUMMARY = "e-tasks is a todo program for Openmoko phones"
HOMEPAGE = "http://code.google.com/p/e-tasks/"
AUTHOR = "cchandel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"
SECTION = "e/apps"
DEPENDS = "elementary eina eldbus sqlite3"

inherit autotools

SRCREV = "890f5ee37d1a5fd1ceb2495950d15151d4cf756b"
PV = "0.0.2+gitr${SRCPV}"

SRC_URI = "git://github.com/shr-project/e-tasks.git"
SRC_URI += "file://0001-dbus-stuff-Convert-to-eldbus.patch"

S = "${WORKDIR}/git"

do_install_append() {
    install -d "${D}/${datadir}/pixmaps"
    install -m 0644 "${S}/resources/e-tasks.png" "${D}/${datadir}/pixmaps"
    install -d "${D}/${datadir}/applications"
    install -m 0644 "${S}/resources/e-tasks.desktop" "${D}/${datadir}/applications"
    install -d  "${D}/${datadir}/e-tasks"
    for ico in "${S}/resources/"*.png; do
        if [ "$(basename $ico)" != "e-tasks.png" ]; then
            install -m 0644 $ico "${D}/${datadir}/e-tasks"
        fi
    done
}

FILES_${PN} += "/usr/share/e-tasks/* /usr/share/applications/* /usr/share/pixmaps/*"
