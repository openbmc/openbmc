SUMMARY = "Mail processing program"
DESCRIPTION = "Procmail can be used to create mail-servers, mailing lists, sort your\
incoming mail into separate folders/files (real convenient when subscribing\
to one or more mailing lists or for prioritising your mail), preprocess\
your mail, start any programs upon mail arrival (e.g. to generate different\
chimes on your workstation for different types of mail) or selectively\
forward certain incoming mail automatically to someone."
HOMEPAGE = "http://www.procmail.org/"
SECTION = "Applications/System"

SRCREV = "07e769f07102767242edf835e995db6646bba373"
SRC_URI = "git://github.com/BuGlessRB/procmail;protocol=https;branch=master;tag=v${PV} \
           file://procmail-3.24-consolidated_fixes-1.patch \
          "

LICENSE = "GPL-2.0-only & Artistic-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=a71e50e197a992c862379e576e669757 \
    file://Artistic;md5=505e00d03c3428cde21b17b2a386590e"

DEPENDS = "libnet libnsl2"

inherit autotools-brokensep

do_configure() {
    find examples -type f | xargs chmod 644
    export CC="${BUILD_CC} -std=gnu17 -Wno-implicit-function-declaration"
    export LD="${BUILD_LD}"
    export CFLAGS="${BUILD_CFLAGS}"
    export AR="${BUILD_AR}"
    export AS="${BUILD_AS}"
    oe_runmake TARGET_CFLAGS="$TARGET_CFLAGS -D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64" LDFLAGS0="${BUILD_LDFLAGS}" LOCKINGTEST=100 autoconf.h
}

do_compile() {
    oe_runmake -i CFLAGS="$TARGET_CFLAGS -std=gnu17 -D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64 LOCKINGTEST=/tmp" LDFLAGS0="${LDFLAGS}" LOCKINGTEST=100
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${mandir}/man1
    install -d ${D}${mandir}/man5
    oe_runmake -i BASENAME=${D}/usr MANDIR=${D}${mandir} LOCKINGTEST=100 LDFLAGS0="${LDFLAGS}" install
}

CVE_STATUS[CVE-1999-0475] = "fixed-version: No action required. The current version (3.24) is not affected by the CVE."
