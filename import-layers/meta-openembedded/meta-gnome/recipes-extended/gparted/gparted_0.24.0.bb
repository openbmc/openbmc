SUMMARY = "A partition editor to graphically manage disk partitions "
HOMEPAGE = "http://gparted.org/index.php"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools pkgconfig gtk-icon-cache

SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${BPN}-${PV}/${BPN}-${PV}.tar.bz2 \
    file://org.yoctoproject.pkexec.run-gparted.policy \
    file://gparted_polkit \
"
SRC_URI[md5sum] = "b8480274c68876acff5965d4346710e7"
SRC_URI[sha256sum] = "02398ab33894a59b0bd8707e598c46d8bb56f1413cd54de48eed61e2920ecd60"

DEPENDS = "glib-2.0 gtkmm parted gnome-doc-utils-native"

do_install_append() {
    # Add a script which checks if polkit is installed.
    # If yes: a policy is requested from polkit / otherwise start as usual
    install ${WORKDIR}/gparted_polkit ${D}${sbindir}
    sed -i 's:%sbindir%:${sbindir}:g' ${D}${sbindir}/gparted_polkit
    # relink menu entry to use our script
    sed -i 's:${sbindir}/gparted:${sbindir}/gparted_polkit:g' ${D}${datadir}/applications/gparted.desktop

    install -d ${D}${datadir}/polkit-1/actions
    install ${WORKDIR}/org.yoctoproject.pkexec.run-gparted.policy ${D}${datadir}/polkit-1/actions/org.yoctoproject.pkexec.run-gparted.policy
}

EXTRA_OECONF = "--disable-scrollkeeper --disable-doc"

FILES_${PN} += " \
    ${datadir}/appdata \
    ${datadir}/icons \
    ${datadir}/polkit-1 \
"

RDEPENDS_${PN} = "dosfstools mtools e2fsprogs"
