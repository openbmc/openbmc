SUMMARY = "A collection of cpuburn programs tuned for different ARM hardware"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://cpuburn-a53.S;beginline=1;endline=22;md5=3b7ccd70144c16d3fe14ac491c2d4a87"

RPROVIDES_${PN} = "cpuburn-neon"
PROVIDES += "cpuburn-neon"

SRCREV = "ad7e646700d14b81413297bda02fb7fe96613c3f"

PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/ssvb/cpuburn-arm.git \
           file://0001-cpuburn-a8.S-Remove-.func-.endfunc.patch \
           file://0002-burn.S-Add.patch \
           file://0003-burn.S-Remove-.func-.endfunc.patch \
           "

S = "${WORKDIR}/git"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} burn.S -o burn
    ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a7.S -o burn-a7
    ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a8.S -o burn-a8
    ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a9.S -o burn-a9
    ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a53.S -o burn-a53
    ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-krait.S -o burn-krait
}

do_install() {
    install -d ${D}${bindir}
    for f in burn burn-a7 burn-a8 burn-a9 burn-a53 burn-krait; do
        install -m 0755 $f ${D}${bindir}/$f
    done
}

COMPATIBLE_MACHINE ?= "(^$)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
