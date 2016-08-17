SUMMARY = "A user-space tool to show and modify the state of GPIOs on the S3c24xx platform"
SECTION = "console/utils"
AUTHOR = "Werner Almesberger <werner@openmoko.org>"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://gpio.c;endline=12;md5=cfb91c686857b2e60852b4925d90a3e1"
SRCREV = "4949"
PV = "1.0+svnr${SRCPV}"
PR = "r2"

SRC_URI = "svn://svn.openmoko.org/trunk/src/target;module=gpio;protocol=http"
S = "${WORKDIR}/gpio"

CLEANBROKEN = "1"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -static -o ${PN} gpio.c
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${PN} ${D}${sbindir}
}
