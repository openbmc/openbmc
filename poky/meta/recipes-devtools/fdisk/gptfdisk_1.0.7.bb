SUMMARY = "Utility for modifying GPT disk partitioning"
DESCRIPTION = "GPT fdisk is a disk partitioning tool loosely modeled on Linux fdisk, but used for modifying GUID Partition Table (GPT) disks. The related FixParts utility fixes some common problems on Master Boot Record (MBR) disks."
HOMEPAGE = "https://sourceforge.net/projects/gptfdisk/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "util-linux"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${PV}/${BP}.tar.gz \
           file://0001-gptcurses-correctly-include-curses.h.patch \
           "
SRC_URI[sha256sum] = "754004b7f85b279287c7ac3c0469b1d7e0eae043a97a2e587b0560ca5f3828c0"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/gptfdisk/files/gptfdisk/"
UPSTREAM_CHECK_REGEX = "/gptfdisk/(?P<pver>(\d+[\.\-_]*)+)/"

EXTRA_OEMAKE = "'CC=${CC}' 'CXX=${CXX}' gdisk fixparts ${PACKAGECONFIG_CONFARGS}"

PACKAGECONFIG ??= "ncurses popt"
PACKAGECONFIG[ncurses] = "cgdisk,,ncurses"
PACKAGECONFIG[popt] = "sgdisk,,popt"

do_install() {
    install -d ${D}${sbindir}
    for f in cgdisk sgdisk; do
        if [ -x $f ]; then
            install -m 0755 $f ${D}${sbindir}
        fi
    done
    install -m 0755 gdisk ${D}${sbindir}
    install -m 0755 fixparts ${D}${sbindir}
}

BBCLASSEXTEND = "native nativesdk"
