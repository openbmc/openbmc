SUMMARY = "The canonical example of init scripts"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/COPYRIGHT;md5=349c872e0066155e1818b786938876a4"

SRC_URI = "file://skeleton \
	   file://skeleton_test.c \
	   file://COPYRIGHT \
	   "

do_compile () {
	${CC} ${CFLAGS} ${LDFLAGS} ${WORKDIR}/skeleton_test.c -o ${WORKDIR}/skeleton-test
}

do_install () {
	install -d ${D}${sysconfdir}/init.d
	cat ${WORKDIR}/skeleton | \
	  sed -e 's,/etc,${sysconfdir},g' \
	      -e 's,/usr/sbin,${sbindir},g' \
	      -e 's,/var,${localstatedir},g' \
	      -e 's,/usr/bin,${bindir},g' \
	      -e 's,/usr,${prefix},g' > ${D}${sysconfdir}/init.d/skeleton
	chmod a+x ${D}${sysconfdir}/init.d/skeleton

	install -d ${D}${sbindir}
	install -m 0755 ${WORKDIR}/skeleton-test ${D}${sbindir}/
}

RDEPENDS_${PN} = "initscripts"

CONFFILES_${PN} += "${sysconfdir}/init.d/skeleton"
