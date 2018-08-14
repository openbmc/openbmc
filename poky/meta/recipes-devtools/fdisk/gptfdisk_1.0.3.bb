SUMMARY = "Utility for modifying GPT disk partitioning"
DESCRIPTION = "GPT fdisk is a disk partitioning tool loosely modeled on Linux fdisk, but used for modifying GUID Partition Table (GPT) disks. The related FixParts utility fixes some common problems on Master Boot Record (MBR) disks."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "util-linux popt ncurses"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "07b625a583b66c8c5840be5923f3e3fe"
SRC_URI[sha256sum] = "89fd5aec35c409d610a36cb49c65b442058565ed84042f767bba614b8fc91b5c"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/gptfdisk/files/gptfdisk/"
UPSTREAM_CHECK_REGEX = "/gptfdisk/(?P<pver>(\d+[\.\-_]*)+)/"

EXTRA_OEMAKE = "'CC=${CC}' 'CXX=${CXX}'"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 cgdisk ${D}${sbindir}
    install -m 0755 gdisk ${D}${sbindir}
    install -m 0755 sgdisk ${D}${sbindir}
    install -m 0755 fixparts ${D}${sbindir}
}

BBCLASSEXTEND = "native nativesdk"
