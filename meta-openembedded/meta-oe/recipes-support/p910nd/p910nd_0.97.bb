DESCRIPTION = "A small network printer daemon for embedded situations that passes the job directly to the printer"
HOMEPAGE = "http://p910nd.sourceforge.net/"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=3d82780e8917b360cbee7b9ec3e40734"

PR = "r2"

# v0.97
SRCREV = "57ebc07ad8723ea4106090536c58c7f7160743e2"
SRC_URI = "git://github.com/kenyapcomau/p910nd;protocol=https;branch=master \
           file://fix-var-lock.patch"

S = "${WORKDIR}/git"

inherit update-rc.d

INITSCRIPT_NAME = "p910nd"
INITSCRIPT_PARAMS = "start 90 2 3 4 5 . stop 60 0 1 6 ."

do_compile () {
    ${CC} ${CFLAGS} ${LDFLAGS} -o p910nd p910nd.c
}

do_install () {
    install -D -m 0755 ${S}/p910nd ${D}${sbindir}/p910nd
    install -D -m 0644 ${S}/aux/p910nd.conf ${D}${sysconfdir}/sysconfig/p910nd.conf
    install -D -m 0644 ${S}/aux/p910nd.init ${D}${sysconfdir}/init.d/p910nd
}
