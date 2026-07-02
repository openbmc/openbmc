SUMMARY = "Cache and memory benchmarking tool"
DESCRIPTION = "RAMspeed is a micro-benchmark for measuring cache and memory bandwidth"
HOMEPAGE = "https://github.com/cruvolo/ramspeed"

LICENSE = "Alasir"
LIC_FILES_CHKSUM = "file://LICENCE;md5=92cffec6695a20eab8d0e4770f4e9353"
LICENSE_FLAGS = "commercial"

SRC_URI = "git://github.com/cruvolo/ramspeed.git;protocol=https;branch=master"
SRCREV = "f3a766dc4f89cee97b5283d5c6bdf8b8e3474813"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} \
        ramspeed.c intmem.c fltmem.c intmark.c fltmark.c \
        -o ramspeed
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ramspeed ${D}${bindir}/
}
