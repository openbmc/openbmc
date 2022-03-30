SUMMARY = "Protocol plugin for New Yahoo (2016) for Adium, Pidgin, Miranda and Telepathy IM Framework"
SECTION = "webos/services"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

DEPENDS = "pidgin json-glib glib-2.0"

inherit pkgconfig

SRC_URI = "git://github.com/EionRobb/funyahoo-plusplus;branch=master;protocol=https"
SRCREV = "fbbd9c591100aa00a0487738ec7b6acd3d924b3f"

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake CC="${CC}" CXX="${CXX}" EXTRA_INCLUDES="${TARGET_CFLAGS}" AR="${AR}";
}

do_install() {
    oe_runmake DESTDIR="${D}" install;
}

FILES:${PN} += " \
    ${libdir} \
"
