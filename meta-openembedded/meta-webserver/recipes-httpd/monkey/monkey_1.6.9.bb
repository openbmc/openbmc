SUMMARY = "Fast and Lightweight HTTP Server for Linux"
HOMEPAGE = "http://monkey-project.com"
BUGTRACKER = "https://github.com/monkey/monkey/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SECTION = "net"

SRC_URI = "git://github.com/monkey/monkey;branch=1.6;protocol=https \
           file://0001-fastcgi-Use-value-instead-of-address-of-sin6_port.patch \
           file://monkey.service \
           file://monkey.init"

SRCREV = "7999b487fded645381d387ec0e057e92407b0d2c"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "https://github.com/monkey/monkey/releases"
UPSTREAM_CHECK_REGEX = "v(?P<pver>\d+(\.\d+)+).tar.gz"

EXTRA_OECMAKE = "-DINSTALL_LOGDIR=${localstatedir}/log/monkey/ \
                 -DPID_FILE=/run/monkey.pid \
                 -DINSTALL_SYSCONFDIR=${sysconfdir}/monkey/ \
                 -DWITH_PLUGINS=* \
                 -DWITHOUT_PLUGINS=mbedtls \
                 -DWITH_DEBUG=1 \
                 -DDEFAULT_USER='www-data' \
                 -DWITH_SYSTEM_MALLOC=1 \
                "

EXTRA_OECMAKE:append:libc-musl = " -DWITH_MUSL=1 "

# GCC-10+ defaults to -fno-common
CFLAGS += "-fcommon"

DISABLE_STATIC = ""

inherit cmake pkgconfig update-rc.d systemd

OECMAKE_GENERATOR = "Unix Makefiles"

do_configure:append() {
    sed -i -e 's|${STAGING_BINDIR_TOOLCHAIN}/||g' ${S}/include/monkey/mk_env.h
}

do_install:append() {
    rmdir ${D}${localstatedir}/log/${BPN} ${D}${localstatedir}/run ${D}${localstatedir}/log
    rmdir --ignore-fail-on-non-empty ${D}${localstatedir}
    install -Dm 0755 ${UNPACKDIR}/monkey.init ${D}${sysconfdir}/init.d/monkey
    # Create /var/log/monkey in runtime.
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -d ${D}${nonarch_libdir}/tmpfiles.d
        echo "d ${localstatedir}/log/${BPN} 0755 ${BPN} ${BPN} -" > ${D}${nonarch_libdir}/tmpfiles.d/${BPN}.conf
    fi
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'sysvinit', d)}" ]; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d ${BPN} ${BPN} 0755 ${localstatedir}/log/${BPN} none" > ${D}${sysconfdir}/default/volatiles/99_${BPN}
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -Dm 644 ${UNPACKDIR}/monkey.service ${D}/${systemd_unitdir}/system/monkey.service
    fi
}

INITSCRIPT_NAME = "monkey"
INITSCRIPT_PARAMS = "defaults 70"

SYSTEMD_SERVICE:${PN} = "monkey.service"

PACKAGES += "${PN}-plugins"

FILES:${PN}-plugins = "${libdir}/monkey-*.so"

FILES:${PN} += "${nonarch_libdir}/tmpfiles.d"

CONFFILES:${PN} = "${sysconfdir}/monkey/monkey.conf \
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

CVE_STATUS[CVE-2013-2183] = "cpe-incorrect: Current version (1.6.9) is not affected. Issue was addressed in version 1.3.0"
