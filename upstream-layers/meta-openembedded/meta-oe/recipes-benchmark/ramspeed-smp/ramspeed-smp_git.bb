SUMMARY = "RAMspeed/SMP cache and memory benchmarking tool"
DESCRIPTION = "RAMspeed/SMP measures cache and memory bandwidth on multiprocessor systems."
HOMEPAGE = "https://github.com/cruvolo/ramspeed-smp"
LICENSE = "LicenseRef-Alasir"
LIC_FILES_CHKSUM = "file://LICENCE;md5=92cffec6695a20eab8d0e4770f4e9353"

SRC_URI = "git://github.com/cruvolo/ramspeed-smp.git;protocol=https;branch=master"
SRCREV = "2011a256caa6a0ccdb88cb8c6b1e69c8f782b729"

UPSTREAM_CHECK_COMMITS = "1"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} \
        ramsmp.c intmem.c fltmem.c intmark.c fltmark.c \
        -o ramsmp
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ramsmp ${D}${bindir}/ramsmp
}
