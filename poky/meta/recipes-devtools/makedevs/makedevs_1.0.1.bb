SUMMARY = "Tool for creating device nodes"
DESCRIPTION = "${SUMMARY}"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://makedevs.c;beginline=2;endline=2;md5=c3817b10013a30076c68a90e40a55570"
SECTION = "base"
SRC_URI = "file://makedevs.c"

S = "${WORKDIR}"

FILES:${PN}:append:class-nativesdk = " ${datadir}"

do_compile() {
	${CC} ${CFLAGS} ${LDFLAGS} -o ${S}/makedevs ${S}/makedevs.c
}

do_install() {
	install -d ${D}${base_sbindir}
	install -m 0755 ${S}/makedevs ${D}${base_sbindir}/makedevs
}

do_install:append:class-nativesdk() {
	install -d ${D}${datadir}
	install -m 644 ${COREBASE}/meta/files/device_table-minimal.txt ${D}${datadir}/
}

BBCLASSEXTEND = "native nativesdk"
