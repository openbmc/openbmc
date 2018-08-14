SUMMARY = "Report application memory usage in a meaningful way"
DESCRIPTION = "smem is a tool that can give numerous reports on memory usage on Linux \
systems. Unlike existing tools, smem can report proportional set size (PSS), \
which is a more meaningful representation of the amount of memory used by \
libraries and applications in a virtual memory system."
HOMEPAGE = "http://www.selenic.com/smem/"
SECTION = "Applications/System"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://www.selenic.com/${BPN}/download/${BP}.tar.gz"
SRC_URI[md5sum] = "fe79435c3930389bfdb560255c802162"
SRC_URI[sha256sum] = "2ea9f878f4cf3c276774c3f7e2a41977a1f2d64f98d2dcb6a15f1f3d84df61ec"

do_compile() {
        ${CC} ${CFLAGS} ${LDFLAGS} smemcap.c -o smemcap
}

do_install() {
        install -d ${D}/${bindir}/
        install -d ${D}/${mandir}/man8
        install -m 0755 ${S}/smem ${D}${bindir}/
        install -m 0755 ${S}/smemcap ${D}${bindir}/
        install -m 0644 ${S}/smem.8 ${D}/${mandir}/man8/
}
RDEPENDS_${PN} += "python-textutils python-compression python-shell python-codecs"
