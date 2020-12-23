SUMMARY = "Fast and Lightweight HTTP Server for Linux"
HOMEPAGE = "http://monkey-project.com"
BUGTRACKER = "https://github.com/monkey/monkey/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SECTION = "net"

SRC_URI = "http://monkey-project.com/releases/1.6/monkey-${PV}.tar.gz \
           file://monkey.service \
           file://monkey.init"

SRC_URI[sha256sum] = "f1122e89cda627123286542b0a18fcaa131cbe9d4f5dd897d9455157289148fb"

UPSTREAM_CHECK_URI = "https://github.com/monkey/monkey/releases"
UPSTREAM_CHECK_REGEX = "v(?P<pver>\d+(\.\d+)+).tar.gz"

EXTRA_OECMAKE = "-DINSTALL_LOGDIR=${localstatedir}/log/monkey/ \
                 -DPID_FILE=${localstatedir}/run/monkey.pid \
                 -DINSTALL_SYSCONFDIR=${sysconfdir}/monkey/ \
                 -DWITH_PLUGINS=* \
                 -DWITHOUT_PLUGINS=mbedtls \
                 -DWITH_DEBUG=1 \
                 -DDEFAULT_USER='www-data' \
                 -DWITH_SYSTEM_MALLOC=1 \
                "

EXTRA_OECMAKE_append_libc-musl = " -DWITH_MUSL=1 "

# GCC-10+ defaults to -fno-common
CFLAGS += "-fcommon"

DISABLE_STATIC = ""

inherit cmake pkgconfig update-rc.d systemd

OECMAKE_GENERATOR = "Unix Makefiles"

do_install_append() {
    rm -rf ${D}/run
    rm -rf ${D}${localstatedir}/run
    install -Dm 0755 ${WORKDIR}/monkey.init ${D}${sysconfdir}/init.d/monkey

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -Dm 644 ${WORKDIR}/monkey.service ${D}/${systemd_unitdir}/system/monkey.service
    fi
}

INITSCRIPT_NAME = "monkey"
INITSCRIPT_PARAMS = "defaults 70"

SYSTEMD_SERVICE_${PN} = "monkey.service"

PACKAGES += "${PN}-plugins"

FILES_${PN}-plugins = "${libdir}/monkey-*.so"

FILES_${PN} += "${localstatedir}/www/monkey/ /run"


CONFFILES_${PN} = "${sysconfdir}/monkey/monkey.conf \
                   ${sysconfdir}/monkey/sites/default \
                   ${sysconfdir}/monkey/monkey.mime \
                   ${sysconfdir}/monkey/plugins.load \
                   ${sysconfdir}/monkey/plugins/proxy_reverse/proxy_reverse.conf \
                   ${sysconfdir}/monkey/plugins/mandril/mandril.conf \
                   ${sysconfdir}/monkey/plugins/fastcgi/fastcgi.conf \
                   ${sysconfdir}/monkey/plugins/logger/logger.conf \
                   ${sysconfdir}/monkey/plugins/cgi/cgi.conf \
                   ${sysconfdir}/monkey/plugins/cheetah/cheetah.conf \
                   ${sysconfdir}/monkey/plugins/dirlisting/dirhtml.conf \
                   ${sysconfdir}/monkey/plugins/dirlisting/themes/guineo/header.theme \
                   ${sysconfdir}/monkey/plugins/dirlisting/themes/guineo/footer.theme \
                   ${sysconfdir}/monkey/plugins/dirlisting/themes/guineo/entry.theme \
                   ${sysconfdir}/monkey/plugins/auth/README \
                   ${sysconfdir}/monkey/plugins/auth/monkey.users \
                   "

