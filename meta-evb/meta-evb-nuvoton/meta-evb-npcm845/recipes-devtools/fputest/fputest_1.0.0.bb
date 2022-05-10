SUMMARY = "An FPU Test Program"
DESCRIPTION = "The FPU test."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5eb289217c160e2920d2e35bddc36453"
SRC_URI = "file://${BPN}"

S = "${WORKDIR}/${BPN}"

LDFLAGS="-lm -lrt"
CFLAGS:remove = "-O2 "
CXXFLAGS:remove = "-O2 "
CFLAGS:prepend = "-O3 "
CXXFLAGS:prepend = "-O3 "
do_install () {
	install -d ${D}${bindir}/
        install busspeed ${D}${bindir}
        install linpack ${D}${bindir}
        install dhry ${D}${bindir}
        install fft1 ${D}${bindir}
        install fft3c ${D}${bindir}
        install linpackneon ${D}${bindir}
        install linpacksp ${D}${bindir}
        install lloops2 ${D}${bindir}
        install memspeed ${D}${bindir}
        install neonspeed ${D}${bindir}
        install whets ${D}${bindir}
}
