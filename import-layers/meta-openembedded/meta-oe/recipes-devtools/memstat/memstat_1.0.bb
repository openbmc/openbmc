SUMMARY = "Display virtual memory allocation"
DESCRIPTION = "Lists all the processes, executables, and shared libraries \
that are using up virtual memory. It's helpful to see how the shared memory \
is used and which 'old' libs are loaded. \
"
HOMEPAGE = "http://memstattool.sourceforge.net/"
SECTION = "devtool"

LICENSE = "GPLv2"

S = "${WORKDIR}/memstattool"

LIC_FILES_CHKSUM = "file://debian/copyright;md5=87be186443b1ac2cfa466f475e1ee0cb"

SRC_URI = "http://sourceforge.net/projects/memstattool/files/memstat_${PV}.tar.gz \
           file://0001-Include-limits.h-for-PATH_MAX-definition.patch \
           "

SRC_URI[md5sum] = "2c3acc0c62b2a18f6601b84e54aa7462"
SRC_URI[sha256sum] = "245d5fc7fb87bcfd14486cd34917cae2856e799559ac568434af12c4852bce94"

do_install_append(){
    install -d ${D}${bindir}
    install -m 0755 memstat ${D}${bindir}
    install -d ${D}${sysconfdir}
    install -m 0755 memstat.conf ${D}${sysconfdir}
    install -d ${D}${mandir}/man1
    install -m 0644 memstat.1 ${D}${mandir}/man1
    install -d ${D}${docdir}/${BPN}
    install -m 0644 memstat-tutorial.txt ${D}${docdir}/${BPN}
}
