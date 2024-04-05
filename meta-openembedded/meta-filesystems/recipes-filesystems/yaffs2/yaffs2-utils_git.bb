SUMMARY = "Yet Another Flash File System"
DESCRIPTION = "Tools for managing 'yaffs2' file systems."

SECTION = "base"
HOMEPAGE = "http://www.yaffs.net"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://utils/mkyaffs2image.c;beginline=11;endline=13;md5=5f5464f9b3e981ca574e65b00e438561 \
                    file://utils/mkyaffsimage.c;beginline=10;endline=12;md5=5f5464f9b3e981ca574e65b00e438561 \
                    "

# The commit date of SRCREV
PV = "20221209"

DEPENDS = "mtd-utils"

# Source is the HEAD of master branch at the time of writing this recipe
SRC_URI = "git://www.aleph1.co.uk/yaffs2;protocol=git;branch=master \
           file://makefile-add-ldflags.patch \
           file://0001-define-loff_t-if-not-already-defined.patch \
           file://0001-yaffs_guts.h-define-YTIME_T-if-not-already-defined.patch \
           "

SRCREV = "613a901a229e8a701c18f003dd0aee18453e0670"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

CFLAGS:append = " -I.. -DCONFIG_YAFFS_UTIL -DCONFIG_YAFFS_DEFINES_TYPES"
EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
    cd utils && oe_runmake
}

INSTALL_FILES = "mkyaffsimage \
                 mkyaffs2image \
                "
do_install() {
    install -d ${D}${sbindir}/
    for i in ${INSTALL_FILES}; do
        install -m 0755 utils/$i ${D}${sbindir}/
    done
}

BBCLASSEXTEND = "native"

# Fixed make clean error:
#make -C /lib/modules/4.4.0-112-generic/build M=<snip>
#make: *** /lib/modules/4.4.0-112-generic/build: No such file or directory.  Stop.
#make: *** [clean] Error 2
CLEANBROKEN = "1"
