DESCRIPTION = "Tiny XML Library"
LICENSE = "Mini-XML-License"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6ba38606d63bb042c5d8cfee182e120"
HOMEPAGE = "https://www.msweet.org/mxml/"
BUGTRACKER = "https://github.com/michaelrsweet/mxml/issues"

SRC_URI = "git://github.com/michaelrsweet/mxml.git"
SRCREV = "ba3cca82e15a88a9cc6afb60f059288a99afc703"
S = "${WORKDIR}/git"
PV = "2.12+git${SRCPV}"

CONFIGUREOPTS = " --prefix=${prefix} \
                  --bindir=${bindir} \
                  --sbindir=${sbindir} \
                  --libexecdir=${libexecdir} \
                  --datadir=${datadir} \
                  --sysconfdir=${sysconfdir} \
                  --sharedstatedir=${sharedstatedir} \
                  --localstatedir=${localstatedir} \
                  --libdir=${libdir} \
                  --includedir=${includedir} \
                  --oldincludedir=${oldincludedir} \
                  --infodir=${infodir} \
                  --mandir=${mandir} \
                  --host=${TARGET_SYS} \
                  --build=${BUILD_SYS} \
                  ${PACKAGECONFIG_CONFARGS} \
                "

do_configure() {
    ./configure ${CONFIGUREOPTS} --enable-shared
}

do_install () {
    export DSTROOT=${D}
    oe_runmake install
}

PACKAGES += " ${PN}-bin "
FILES_${PN} = "${libdir}/*"
FILES_${PN}-bin = "${bindir}/*"
