DESCRIPTION="Simple Login Manager"
HOMEPAGE="http://slim.berlios.de"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

PR = "r1"

DEPENDS = "virtual/libx11 libxmu libpng jpeg freetype sessreg ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

SRC_URI = " \
    http://download.berlios.de/${BPN}/${BP}.tar.gz \
    file://0002-Fix-image-handling-integer-overflows.patch \
    file://0003-Fix-build-failure-with-ld-as-needed.patch \
    file://0004-Add-support-libpng15.patch \
    file://0005-Remove-path-of-gcc-amd-g-and-version-of-g.patch \
    file://0006-Remove-localhost-from-Authenticator-of-pam.patch \
    file://0007-Fix-tty-slowness.patch \
    file://0008-restart-Xserver-if-killed.patch \
    file://slim-dynwm \
    file://update_slim_wmlist \
    file://Makefile.oe \
    file://slim.pamd \
    file://slim.service \
"

SRC_URI[md5sum] = "ca1ae6120e6f4b4969f2d6cf94f47b42"
SRC_URI[sha256sum] = "f1560125005f253b9b88220598fed7a9575ef405716862c6ca3fcc72dbd482b8"


EXTRA_OEMAKE += " \
    USE_PAM=${@bb.utils.contains('DISTRO_FEATURES', 'pam', '1', '0', d)} \
    PREFIX=${prefix} \
    CFGDIR=${sysconfdir} \
    MANDIR=${mandir} \
    DESTDIR=${D} \
    CFLAGS+=-I${STAGING_INCDIR}/freetype2 \
    CXXFLAGS+=-I${STAGING_INCDIR}/freetype2 \
"

do_compile_prepend() {
    cp -pP ${WORKDIR}/Makefile.oe ${S}/Makefile
}

do_install() {
    oe_runmake install
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/slim-dynwm ${D}${bindir}/
    install -m 0755 ${WORKDIR}/update_slim_wmlist ${D}${bindir}/
    install -d ${D}${sysconfdir}/pam.d/
    install -m 0644 ${WORKDIR}/slim.pamd ${D}${sysconfdir}/pam.d/slim

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_unitdir}/system/

    echo 'sessionstart_cmd    /usr/bin/sessreg -a -l $DISPLAY %user' >> ${D}${sysconfdir}/slim.conf
    echo 'sessionstop_cmd     /usr/bin/sessreg -d -l $DISPLAY %user' >> ${D}${sysconfdir}/slim.conf
}


RDEPENDS_${PN} = "perl xauth freetype sessreg "
FILES_${PN} += "${systemd_unitdir}/system/"

pkg_postinst_${PN} () {
if test "x$D" != "x"; then
    exit 1
fi
systemctl enable slim.service

# Register SLiM as default DM
mkdir -p ${sysconfdir}/X11/
echo "${bindir}/slim" > ${sysconfdir}/X11/default-display-manager
}

pkg_postrm_${PN} () {
if test "x$D" != "x"; then
    exit 1
fi
systemctl disable slim.service
sed -i /slim/d $D${sysconfdir}/X11/default-display-manager || true
}

PNBLACKLIST[slim] ?= "does not build with distroless qemuarm as reported in 'State of bitbake world' thread, nobody volunteered to fix them"
