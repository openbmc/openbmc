SUMMARY = "Simple program to read/write from/to any location in memory"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://devmem2.c;endline=38;md5=a9eb9f3890384519f435aedf986297cf"

SRC_URI = "git://github.com/denix0/devmem2.git;protocol=https;branch=main"
SRCREV = "5b395a946894eb4f4ef5d07c80a50a88573a541e"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

CFLAGS += "-DFORCE_STRICT_ALIGNMENT"

do_compile() {
    ${CC} -o devmem2 devmem2.c ${CFLAGS} ${LDFLAGS}
}

do_install() {
    install -d ${D}${bindir}
    install devmem2 ${D}${bindir}
}
