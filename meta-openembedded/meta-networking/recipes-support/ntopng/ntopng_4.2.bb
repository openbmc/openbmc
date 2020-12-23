SUMMARY = "Web-based Traffic and Security Network Traffic Monitoring"
DESCRIPTION = "ntopng is a web-based network traffic monitoring application \
released under GPLv3. It is the new incarnation of the original \
ntop written in 1998, and now revamped in terms of performance, \
usability, and features."

SECTION = "console/network"

DEPENDS = "curl libmaxminddb libpcap lua mariadb ndpi json-c rrdtool zeromq"
RDEPENDS_${PN} = "bash redis"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRCREV = "5e649a2d1130b4a3ab0c5bb673d615172cc0bdbb"
SRC_URI = "git://github.com/ntop/ntopng.git;protocol=git;branch=4.2-stable \
           file://0001-configure.seed-fix-configure-error.patch \
           file://0001-configure.seed-fix-host-contamination.patch \
           file://0001-Makefile.in-don-t-use-the-internal-lua.patch \
           file://0001-autogen.sh-generate-configure.ac-only.patch \
           file://0001-configure.seed-not-check-clang-on-host.patch \
           file://ntopng.service \
"

S = "${WORKDIR}/git"

# don't use the lua under thirdparty as it supports cross compiling badly
export LUA_LIB = "${STAGING_LIBDIR}/liblua.a"

LDFLAGS_append_mipsarch = " -latomic"
LDFLAGS_append_powerpc = " -latomic"
LDFLAGS_append_riscv32 = " -latomic"
inherit autotools-brokensep gettext systemd

do_install_append() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/ntopng.service ${D}${systemd_unitdir}/system
}

FILES_${PN} += "\
    ${systemd_unitdir}/system/ntopng.service"

FILES_${PN}-doc += "\
    /usr/man/man8/ntopng.8"

do_configure_prepend() {
    ${S}/autogen.sh
}

SYSTEMD_SERVICE_${PN} = "ntopng.service"
