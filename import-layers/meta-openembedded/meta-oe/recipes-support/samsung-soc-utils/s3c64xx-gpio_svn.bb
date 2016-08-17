SUMMARY = "A user-space tool to show and modify the state of GPIOs on the S3c64xx platform"
SECTION = "console/utils"
AUTHOR = "Werner Almesberger <werner@openmoko.org>"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://gpio-s3c6410.c;endline=12;md5=060cda1be945ad9194593f11d56d55c7"
SRCREV = "4949"
PV = "1.0+svnr${SRCPV}"

SRC_URI = "svn://svn.openmoko.org/trunk/src/target;module=gpio;protocol=http"
S = "${WORKDIR}/gpio"

CLEANBROKEN = "1"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -static -o ${PN} gpio-s3c6410.c
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${PN} ${D}${sbindir}
}
