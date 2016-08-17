SUMMARY = "Tvheadend TV streaming server"
HOMEPAGE = "https://www.lonelycoder.com/redmine/projects/tvheadend"

DEPENDS = "avahi zlib openssl python-native"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9eef91148a9b14ec7f9df333daebc746"

SRC_URI = "git://github.com/tvheadend/tvheadend.git \
           file://0001-Move-tvheadend-specific-LD-CFLAGS-into-a-helper-vari.patch \
"
SRCREV = "a420c83a0e0d2c31c2c15d0fec6fedc3f5a36dfe"
PV = "3.3"

S = "${WORKDIR}/git"

do_configure() {
    ./configure --prefix=${prefix} \
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
