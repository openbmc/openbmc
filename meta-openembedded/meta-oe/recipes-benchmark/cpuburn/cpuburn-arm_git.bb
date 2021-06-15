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

    # If the arch is set to ARM 64-bit -  we only produce and ship burn-a53 version.
    # In case of ARM 32-bit - we would build all variants, since burn-a53 supports both
    # 32 and 64-bit builds
    if ${@bb.utils.contains('TUNE_FEATURES', 'aarch64', 'true', 'false', d)}; then
        ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a53.S -o burn-a53
    else
        ${CC} ${CFLAGS} ${LDFLAGS} burn.S -o burn
        ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a7.S -o burn-a7
        ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a8.S -o burn-a8
        ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a9.S -o burn-a9
        ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a53.S -o burn-a53
        ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-krait.S -o burn-krait
    fi
}

do_install() {
    install -d ${D}${bindir}

    if ${@bb.utils.contains('TUNE_FEATURES', 'aarch64', 'true', 'false', d)}; then
        install -m 0755 burn-a53 ${D}${bindir}
    else
        for f in burn burn-a7 burn-a8 burn-a9 burn-a53 burn-krait; do
            install -m 0755 $f ${D}${bindir}/$f
        done
    fi
}

COMPATIBLE_MACHINE ?= "(^$)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"
