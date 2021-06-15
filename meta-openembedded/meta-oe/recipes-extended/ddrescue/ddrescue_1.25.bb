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
SRC_URI[md5sum] = "99fd7a28bf9953d88534c7ee9ab5bd2a"
SRC_URI[sha256sum] = "ce538ebd26a09f45da67d3ad3f7431932428231ceec7a2d255f716fa231a1063"

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
