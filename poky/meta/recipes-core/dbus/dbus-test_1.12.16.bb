SUMMARY = "D-Bus test package (for D-bus functionality testing only)"
HOMEPAGE = "http://dbus.freedesktop.org"
SECTION = "base"
LICENSE = "AFL-2.1 | GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=10dded3b58148f3f1fd804b26354af3e \
                    file://dbus/dbus.h;beginline=6;endline=20;md5=7755c9d7abccd5dbd25a6a974538bb3c"

DEPENDS = "dbus glib-2.0"

RDEPENDS_${PN}-dev = ""

SRC_URI = "http://dbus.freedesktop.org/releases/dbus/dbus-${PV}.tar.gz \
           file://tmpdir.patch \
           file://run-ptest \
           file://python-config.patch \
           file://clear-guid_from_server-if-send_negotiate_unix_f.patch \
           "

SRC_URI[md5sum] = "2dbeae80dfc9e3632320c6a53d5e8890"
SRC_URI[sha256sum] = "54a22d2fa42f2eb2a871f32811c6005b531b9613b1b93a0d269b05e7549fec80"

S="${WORKDIR}/dbus-${PV}"
FILESEXTRAPATHS =. "${FILE_DIRNAME}/dbus:"

inherit autotools pkgconfig gettext ptest upstream-version-is-even

EXTRA_OECONF_X = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '--with-x', '--without-x', d)}"
EXTRA_OECONF_X_class-native = "--without-x"

EXTRA_OECONF = "--enable-tests \
                --enable-modular-tests \
                --enable-installed-tests \
                --enable-checks \
                --enable-asserts \
                --enable-largefile \
                --disable-xml-docs \
                --disable-doxygen-docs \
                --disable-libaudit \
                --with-dbus-test-dir=${PTEST_PATH} \
                ${EXTRA_OECONF_X} \
                --enable-embedded-tests \
             "

EXTRA_OECONF_append_class-target = " SYSTEMCTL=${base_bindir}/systemctl"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd x11', d)}"
PACKAGECONFIG_class-native = ""
PACKAGECONFIG_class-nativesdk = ""

PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_system_unitdir},--disable-systemd --without-systemdsystemunitdir,systemd"
PACKAGECONFIG[x11] = "--with-x --enable-x11-autolaunch,--without-x --disable-x11-autolaunch, virtual/libx11 libsm"
PACKAGECONFIG[user-session] = "--enable-user-session --with-systemduserunitdir=${systemd_user_unitdir},--disable-user-session"
PACKAGECONFIG[verbose-mode] = "--enable-verbose-mode,,,"

do_install() {
    :
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/test
	l="shell printf refs syslog marshal syntax corrupt dbus-daemon dbus-daemon-eavesdrop loopback relay \
	   variant uid-permissions syntax spawn sd-activation names monitor message fdpass service shell-service"
	for i in $l; do install ${B}/test/.libs/test-$i ${D}${PTEST_PATH}/test; done

	l="bus bus-system bus-launch-helper"
	for i in $l; do install ${B}/bus/.libs/test-$i ${D}${PTEST_PATH}/test; done

	install -d ${D}${PTEST_PATH}/bus
	install ${B}/bus/.libs/dbus-daemon-launch-helper-test ${D}${PTEST_PATH}/bus

	install ${B}/test/test-segfault ${D}${PTEST_PATH}/test

	cp -r ${B}/test/data ${D}${PTEST_PATH}/test
	install ${B}/dbus/.libs/test-dbus ${D}${PTEST_PATH}/test

	install -d ${D}${PTEST_PATH}/test/.libs
	cp -a ${B}/dbus/.libs/*.so* ${D}${PTEST_PATH}/test/.libs

	# Remove build host references...
	find "${D}${PTEST_PATH}/test/data" \( -name *.service -o -name *.conf -o -name "*.aaprofile" \) -type f -exec \
		sed -i \
		 -e 's:${B}:${PTEST_PATH}:g' \
		 {} +
        sed -i -e 's;@PTEST_PATH@;${PTEST_PATH};g'  ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS_${PN}-ptest += "bash make dbus"
RDEPENDS_${PN}-ptest_remove = "${PN}"

PRIVATE_LIBS_${PN}-ptest = "libdbus-1.so.3"
