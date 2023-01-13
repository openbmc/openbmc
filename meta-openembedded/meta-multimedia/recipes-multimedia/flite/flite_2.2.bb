SUMMARY = "Flite: a small run-time speech synthesis engine"
HOMEPAGE = "http://cmuflite.org"
SECTION = "libs/multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b3b732d1349633a53e69356607fd2d6c"

inherit autotools-brokensep

DEPENDS += "alsa-lib chrpath-replacement-native"

SRC_URI = "git://github.com/festvox/flite.git;protocol=https;branch=master"

SRCREV = "e9e2e37c329dbe98bfeb27a1828ef9a71fa84f88"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--enable-shared"

do_configure:append() {
    sed -i '/$(INSTALL) -m 755 $(BINDIR)\/flite_time $(DESTDIR)$(INSTALLBINDIR)/d' ${S}/main/Makefile
}

do_install:append() {
    chown -R root:root ${D}${libdir}/*
}
# | make[1]: *** No rule to make target 'flite_voice_list.c', needed by 'all'.  Stop.
PARALLEL_MAKE = ""
