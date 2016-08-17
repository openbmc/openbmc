SUMMARY = "Data recovery tool"
DESCRIPTION = "GNU ddrescue is a data recovery tool. It copies data \
    from one file or block device (hard disc, cdrom, etc) to another, \
    trying hard to rescue data in case of read errors."
HOMEPAGE = "http://www.gnu.org/software/ddrescue/ddrescue.html"
SECTION = "console"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=76d6e300ffd8fb9d18bd9b136a9bba13 \
                    file://main_common.cc;beginline=5;endline=16;md5=3ec288b2676528cd2b069364e313016f"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.lz"
SRC_URI[md5sum] = "ed6b5b82d74cbd925db2b829350e74b1"
SRC_URI[sha256sum] = "ed8d0c9d9aac80a9110e9cb0d0a91a7390d3bf9f816b67a62ca4eb140f4747b8"

# This isn't already added by base.bbclass
do_unpack[depends] += "lzip-native:do_populate_sysroot"

CONFIGUREOPTS = "\
    '--srcdir=${S}' \
    '--prefix=${prefix}' \
    '--exec-prefix=${exec_prefix}' \
    '--bindir=${bindir}' \
    '--datadir=${datadir}' \
    '--infodir=${infodir}' \
    '--sysconfdir=${sysconfdir}' \
    'CXX=${CXX}' \
    'CPPFLAGS=${CPPFLAGS}' \
    'CXXFLAGS=${CXXFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
"
EXTRA_OEMAKE = ""

do_configure () {
    ${S}/configure ${CONFIGUREOPTS}
}

do_install () {
    oe_runmake 'DESTDIR=${D}' install
    # Info dir listing isn't interesting at this point so remove it if it exists.
    if [ -e "${D}${infodir}/dir" ]; then
        rm -f ${D}${infodir}/dir
    fi
}

