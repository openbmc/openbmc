SUMMARY = "Edb is the Enlightenment database library"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8a7c08df3bc3280208b959be7215c25 \
                    file://COPYING-PLAIN;md5=f59cacc08235a546b0c34a5422133035"

DEPENDS = "zlib"
PV = "1.0.5.050+svnr${SRCPV}"
SRCREV = "${EFL_SRCREV}"

inherit efl

PACKAGECONFIG ??= ""
PACKAGECONFIG[ncurses] = "--enable-ncurses,--disable-ncurses,ncurses"

SRC_URI = "${E_SVN}/OLD;module=${SRCNAME};protocol=http;scmdata=keep"
S = "${WORKDIR}/${SRCNAME}"
