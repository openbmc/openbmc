SUMMARY = "Astronomical application which displays the night sky"
HOMEPAGE    = "http://projects.openmoko.org/projects/orrery/"
SECTION = "x11/scientific"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://orrery.c;endline=25;md5=d792bdf2b591972da175aecc38d88cfe"
DEPENDS = "gtk+"

inherit autotools-brokensep pkgconfig

SRC_URI = "http://projects.openmoko.org/frs/download.php/923/orrery_2.7_clean.tar.gz \
           file://orrery.png \
           file://use.GdkPixbuf.patch \
"

SRC_URI[md5sum]    = "bd62a33e7554ee1030313dfcdefcda8b"
SRC_URI[sha256sum] = "645166a5e05b2064ab630534a514697fc47b681951e7fe1d635c259cbdf7a5e6"

S = "${WORKDIR}/${BPN}"

do_configure_prepend() {
    # fix DSO issue with binutils-2.22
    sed -i 's/ -lrt/ -lrt -lm/g' ${S}/Makefile.am
}
do_install_append() {
    install -d ${D}${datadir}/orrery
    cp -R --no-dereference --preserve=mode,links -v ${S}/data/* ${D}${datadir}/orrery
    chown -R root:root ${D}${datadir}/orrery
    install -d ${D}${datadir}/icons
    install -m 0755 ${WORKDIR}/orrery.png ${D}${datadir}/icons
}

FILES_${PN} += "${datadir}/icons/orrery.png"
