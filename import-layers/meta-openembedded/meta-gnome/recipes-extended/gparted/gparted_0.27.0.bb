SUMMARY = "A partition editor to graphically manage disk partitions "
HOMEPAGE = "http://gparted.org/index.php"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools pkgconfig gtk-icon-cache

SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${BPN}-${PV}/${BPN}-${PV}.tar.gz \
    file://org.yoctoproject.pkexec.run-gparted.policy \
    file://gparted_polkit \
"
SRC_URI[md5sum] = "726f539f774b442670028af656f270bd"
SRC_URI[sha256sum] = "208cf60c061b86bee4f658d568a3b5d843dce3ab9847f66b452298268799e7bd"

DEPENDS = "glib-2.0 glib-2.0-native gtkmm parted gnome-doc-utils intltool-native"

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
