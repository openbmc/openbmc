SUMMARY = "Utility for modifying GPT disk partitioning"
DESCRIPTION = "GPT fdisk is a disk partitioning tool loosely modeled on Linux fdisk, but used for modifying GUID Partition Table (GPT) disks. The related FixParts utility fixes some common problems on Master Boot Record (MBR) disks."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "util-linux"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${PV}/${BP}.tar.gz \
           file://0001-gptcurses-correctly-include-curses.h.patch \
           "
SRC_URI[md5sum] = "58dac67c85e46ca87b587231549aefe6"
SRC_URI[sha256sum] = "0e7d3987cd0488ecaf4b48761bc97f40b1dc089e5ff53c4b37abe30bc67dcb2f"

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
