SUMMARY = "Report application memory usage in a meaningful way"
DESCRIPTION = "smem is a tool that can give numerous reports on memory usage on Linux \
systems. Unlike existing tools, smem can report proportional set size (PSS), \
which is a more meaningful representation of the amount of memory used by \
libraries and applications in a virtual memory system."
HOMEPAGE = "http://www.selenic.com/smem/"
SECTION = "Applications/System"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

HG_CHANGESET = "98273ce331bb"
SRC_URI = "https://selenic.com/repo/${BPN}/archive/${HG_CHANGESET}.tar.bz2;downloadfilename=${BP}.tar.bz2 \
           file://0001-smem-fix-support-for-source-option-python3.patch"
SRC_URI[sha256sum] = "161131c686a6d9962a0e96912526dd46308e022d62e3f8acaed5a56fda8e08ce"

UPSTREAM_CHECK_URI = "https://selenic.com/repo/smem/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/${BPN}-${HG_CHANGESET}"

do_compile() {
        ${CC} ${CFLAGS} ${LDFLAGS} smemcap.c -o smemcap
}

do_install() {
        install -d ${D}/${bindir}/
        install -d ${D}/${mandir}/man8
        install -m 0755 ${S}/smem ${D}${bindir}/
        sed -i -e '1s,#!.*python.*,#!${USRBINPATH}/env python3,' ${D}${bindir}/smem
        install -m 0755 ${S}/smemcap ${D}${bindir}/
        install -m 0644 ${S}/smem.8 ${D}/${mandir}/man8/
}

RDEPENDS:${PN} = "python3-core python3-compression"
RRECOMMENDS:${PN} = "python3-matplotlib python3-numpy"

PACKAGE_BEFORE_PN = "smemcap"

FILES:smemcap = "${bindir}/smemcap"

BBCLASSEXTEND = "native"
