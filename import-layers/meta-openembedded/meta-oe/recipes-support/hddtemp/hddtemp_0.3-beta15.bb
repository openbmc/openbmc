SUMMARY = "Hard disk temperature monitor daemon"
SECTION = "console/network"
LICENSE = "GPLv2+"

PR = "r1"

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/hddtemp/hddtemp-0.3-beta15.tar.bz2 \
           file://hddtemp-no-nls-support.patch \
           file://hddtemp_0.3-beta15-52.diff \
           file://hddtemp-0.3-beta15-autodetect-717479.patch \
           file://hddtemp.db \
           file://init \
"

SRC_URI[md5sum] = "8b829339e1ae9df701684ec239021bb8"
SRC_URI[sha256sum] = "618541584054093d53be8a2d9e81c97174f30f00af91cb8700a97e442d79ef5b"

LIC_FILES_CHKSUM = "file://GPL-2;md5=eb723b61539feef013de476e68b5c50a"

inherit autotools gettext update-rc.d

FILES_${PN} += "/usr/share/misc/hddtemp.db"

do_install_append() {
    install -d ${D}/usr/share/misc/
    install -m 0644 ${WORKDIR}/hddtemp.db ${D}/usr/share/misc/hddtemp.db
    install -d ${D}${sysconfdir}/init.d
    install -m 0644 ${WORKDIR}/init ${D}${sysconfdir}/init.d/hddtemp
}

INITSCRIPT_NAME = "hddtemp"
INITSCRIPT_PARAMS = "start 99 2 3 4 5 . stop 20 0 1 6 ."
