DESCRIPTION = "Whetstone benchmark is a synthetic benchmark for evaluating the performance of computers"
SUMMARY = "CPU benchmark to measure floating point performance"

LICENSE = "PD"
LIC_FILES_CHKSUM ="file://${UNPACKDIR}/whetstone.c;beginline=1;endline=52;md5=c795edc15e7e1d92ca8f88ad718449f5"

SRC_URI = "http://www.netlib.org/benchmark/whetstone.c"
SRC_URI[sha256sum] = "333e4ceca042c146f63eec605573d16ae8b07166cbc44a17bec1ea97c6f1efbf"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_compile () {
	${CC} ${CFLAGS} ${LDFLAGS} -Ofast -o whetstone whetstone.c -lm
}

do_install () {
	install -Dm 0755 whetstone ${D}${bindir}/whetstone
}

