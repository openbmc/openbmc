SUMMARY = "gnome system tools backends"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "dbus dbus-glib glib-2.0 polkit"

inherit gnome pkgconfig update-rc.d gettext

SRC_URI[archive.md5sum] = "edae148b31342aecae035051adc70c74"
SRC_URI[archive.sha256sum] = "1dbe5177df46a9c7250735e05e77129fe7ec04840771accfa87690111ca2c670"

SRC_URI += " \
            file://system-tools-backends \
"

# This needs to move to meta-angstrom
SRC_URI_append_angstrom = " \
            file://add-angstrom-distro.patch \
"

EXTRA_OECONF = " --with-net-dbus=${libdir}/perl5 "

do_configure() {
    rm missing || true
    automake --add-missing
    sed -i -e 's:CC=$(CC):CC="$(CC)":g' ${S}/Net-DBus/Makefile.am
    sed -i -e 's:CC=$(CC):CC="$(CC)":g' ${S}/Net-DBus/Makefile.in
    libtoolize --force --install
    aclocal
    gnu-configize
    oe_runconf
    cp ${STAGING_BINDIR_CROSS}/${HOST_SYS}-libtool ${S}
}

do_install_append () {
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/system-tools-backends ${D}/${sysconfdir}/init.d/    
}

INITSCRIPT_NAME = "system-tools-backends"
INITSCRIPT_PARAMS = "start 50 2 3 4 5 . stop 70 1 ."

# Shadow added so there is a full adduser/deluser
# (Gnome images tend to pull in shadow anyway)
RDEPENDS_${PN} = "shadow"

FILES_${PN} += " ${sysconfdir}/dbus-1/system.d"
FILES_${PN} += " ${libdir}/pkgconfig"
FILES_${PN} += " ${datadir}/dbus-1/system-services"
FILES_${PN} += " ${datadir}/system-tools-backends-2.0/files"
FILES_${PN} += " ${datadir}/system-tools-backends-2.0/scripts"
FILES_${PN} += " ${datadir}/polkit*"

PNBLACKLIST[system-tools-backends] ?= "does not build with distroless qemuarm as reported in 'State of bitbake world' thread, nobody volunteered to fix them"
