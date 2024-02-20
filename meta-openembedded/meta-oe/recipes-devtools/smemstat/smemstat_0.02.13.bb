SUMMARY = "smemstat reports the physical memory usage taking into consideration shared memory"
HOMEPAGE = "https://github.com/ColinIanKing/smemstat"
LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "ncurses"

SRC_URI = "git://github.com/ColinIanKing/smemstat.git;protocol=https;branch=master"
SRCREV = "1edc560602aa116b96408110b8b9d66a4edef60e"

S = "${WORKDIR}/git"

inherit bash-completion

do_compile () {
    oe_runmake smemstat
}

do_install () {
    oe_runmake DESTDIR=${D} install
}
