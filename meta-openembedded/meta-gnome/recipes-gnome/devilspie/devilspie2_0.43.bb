DESCRIPTION = "Devilspie2 is a window matching utility, allowing the user to perform scripted actions on windows as they are created"
HOMEPAGE = "http://www.gusnan.se/devilspie2"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=00aefaa50aad75c21367df66102d542c \
                    file://GPL3.txt;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "gtk+ glib-2.0 libwnck libxinerama lua virtual/libx11"

PV .= "+git${SRCPV}"

SRCREV = "a3ee65b815a0247a0dbdaa39353444cef6f93499"

SRC_URI = "git://github.com/dsalt/devilspie2;branch=master;protocol=https \
    file://default.lua \
    file://devilspie2.desktop \
"

S = "${WORKDIR}/git"

inherit features_check pkgconfig gettext

REQUIRED_DISTRO_FEATURES = "x11"

do_compile() {
    export GTK2=1
    oe_runmake CC="${CC}" CPPFLAGS="${CPPFLAGS}" LDFLAGS=" -ldl -lm ${LDFLAGS}"
}

do_install() {
    oe_runmake DESTDIR="${D}" PREFIX="${prefix}" install
    install -d ${D}/${sysconfdir}/devilspie2
    install -m 644 ${WORKDIR}/default.lua ${D}/${sysconfdir}/devilspie2
    install -d ${D}/${sysconfdir}/xdg/autostart
    install -m 644 ${WORKDIR}/devilspie2.desktop ${D}/${sysconfdir}/xdg/autostart
}
