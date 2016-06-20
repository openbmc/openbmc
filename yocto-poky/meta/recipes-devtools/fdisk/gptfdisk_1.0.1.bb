SUMMARY = "Utility for modifying GPT disk partitioning"
DESCRIPTION = "GPT fdisk is a disk partitioning tool loosely modeled on Linux fdisk, but used for modifying GUID Partition Table (GPT) disks. The related FixParts utility fixes some common problems on Master Boot Record (MBR) disks."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "util-linux popt ncurses"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "d7f3d306b083123bcc6f5941efade586"
SRC_URI[sha256sum] = "864c8aee2efdda50346804d7e6230407d5f42a8ae754df70404dd8b2fdfaeac7"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/gptfdisk/files/gptfdisk/"
UPSTREAM_CHECK_REGEX = "/gptfdisk/(?P<pver>(\d+[\.\-_]*)+)/"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 cgdisk ${D}${sbindir}
    install -m 0755 gdisk ${D}${sbindir}
    install -m 0755 sgdisk ${D}${sbindir}
    install -m 0755 fixparts ${D}${sbindir}
}

BBCLASSEXTEND = "native nativesdk"
