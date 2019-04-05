DESCRIPTION = "Tiny XML Library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
HOMEPAGE = "https://www.msweet.org/mxml/"
BUGTRACKER = "https://github.com/michaelrsweet/mxml/issues"

SRC_URI = "git://github.com/michaelrsweet/mxml.git"
SRCREV = "c7755b6992a2afdd34dde47fc9be97f1237cfded"
S = "${WORKDIR}/git"
# v3.0
PV = "3.0"

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
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    ./configure ${CONFIGUREOPTS} --enable-shared
}

do_install () {
    export DSTROOT=${D}
    oe_runmake install
}

PACKAGES += " ${PN}-bin "
FILES_${PN} = "${libdir}/*"
FILES_${PN}-bin = "${bindir}/*"
