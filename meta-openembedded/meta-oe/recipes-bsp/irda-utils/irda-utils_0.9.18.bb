SUMMARY = "Common files for IrDA"
DESCRIPTION = "Provides common files needed to use IrDA. \
IrDA allows communication over Infrared with other devices \
such as phones and laptops."
HOMEPAGE = "http://irda.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/p/irda/bugs/"
SECTION = "base"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://irdadump/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://smcinit/COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://man/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://irdadump/irdadump.c;beginline=1;endline=24;md5=d78b9dce3cd78c2220250c9c7a2be178"

SRC_URI = "${SOURCEFORGE_MIRROR}/irda/irda-utils-${PV}.tar.gz \
           file://ldflags.patch \
           file://musl.patch \
           file://init"

SRC_URI[md5sum] = "84dc12aa4c3f61fccb8d8919bf4079bb"
SRC_URI[sha256sum] = "61980551e46b2eaa9e17ad31cbc1a638074611fc33bff34163d10c7a67a9fdc6"

inherit update-rc.d

RRECOMMENDS:${PN} = "\
    kernel-module-pxaficp-ir \
    kernel-module-irda \
    kernel-module-ircomm \
    kernel-module-ircomm-tty \
    kernel-module-irlan \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ppp', 'kernel-module-irnet', '',d)} \
    kernel-module-irport \
    kernel-module-irtty \
    kernel-module-irtty-sir \
    kernel-module-sir-dev \
    ${@bb.utils.contains('COMBINED_FEATURES', 'usbhost', 'kernel-module-ir-usb', '',d)} "

EXTRA_OEMAKE = "\
    'CC=${CC}' \
    'LD=${LD}' \
    'CFLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
    'SYS_INCLUDES=' \
    'V=1' \
"

INITSCRIPT_NAME = "irattach"
INITSCRIPT_PARAMS = "defaults 20"

TARGETS ??= "irattach irdaping"
do_compile () {
	for t in ${TARGETS}; do
		oe_runmake -C $t
	done
}

do_install () {
	install -d ${D}${sbindir}
	for t in ${TARGETS}; do
		oe_runmake -C $t ROOT="${D}" install
	done

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${UNPACKDIR}/init ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
}
