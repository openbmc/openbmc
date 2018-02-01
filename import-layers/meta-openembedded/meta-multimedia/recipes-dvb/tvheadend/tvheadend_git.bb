SUMMARY = "Tvheadend TV streaming server"
HOMEPAGE = "https://www.lonelycoder.com/redmine/projects/tvheadend"

DEPENDS = "avahi zlib openssl python-native dvb-apps"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=9cae5acac2e9ee2fc3aec01ac88ce5db"

SRC_URI = "git://github.com/tvheadend/tvheadend.git;branch=release/4.0 \
           file://0001-Fix-checks-for-sse2-mmx.patch \
           file://0001-disable-varargs-warning-on-clang.patch \
           file://0001-hdhomerun-Override-forced-overrdiing-og-CC-STRIP-and.patch \
           file://0001-dvr-Use-labs-instead-of-abs.patch \
           file://0001-Makefile-Ignore-warning-about-wrong-includes.patch \
"
SRCREV = "64fec8120158de585e18be705055259484518d94"
PV = "4.0.9+git${SRCREV}"

S = "${WORKDIR}/git"

PACKAGECONFIG ?= ""
PACKAGECONFIG[uriparser] = "--enable-uriparser,--disable-uriparser,uriparser"

do_configure() {
    ./configure ${PACKAGECONFIG_CONFARGS} \
                --prefix=${prefix} \
                --libdir=${libdir} \
                --bindir=${bindir} \
                --datadir=${datadir} \
                --arch=${TARGET_ARCH} \
                --disable-dvbscan \
                --disable-bundle
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

FILES_${PN} += "${datadir}/${BPN}"

RDEPENDS_${PN} += "libdvben50221 libucsi libdvbapi"

PNBLACKLIST[tvheadend] ?= "Depends on blacklisted dvb-apps - the recipe will be removed on 2017-09-01 unless the issue is fixed"
