SUMMARY = "D-Bus message bus"
DESCRIPTION = "D-Bus is a message bus system, a simple way for applications to talk to one another. In addition to interprocess communication, D-Bus helps coordinate process lifecycle; it makes it simple and reliable to code a \"single instance\" application or daemon, and to launch applications and daemons on demand when their services are needed."
HOMEPAGE = "https://dbus.freedesktop.org"
SECTION = "base"

inherit meson pkgconfig gettext upstream-version-is-even ptest-gnome

LICENSE = "AFL-2.1 | GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb0ffc69a965797a3d6686baa153ef05 \
                    file://dbus/dbus.h;beginline=6;endline=22;md5=df4251a6c6e15e6a9e3c77b2ac30065d \
                    "

SRC_URI = "https://dbus.freedesktop.org/releases/dbus/dbus-${PV}.tar.xz \
           file://run-ptest \
           file://dbus-1.init \
           "

SRC_URI[sha256sum] = "9f8ca5eb51cbe09951aec8624b86c292990ae2428b41b856e2bed17ec65c8849"

EXTRA_OEMESON = "-Dxml_docs=disabled \
                 -Ddoxygen_docs=disabled \
                 -Dsystem_socket=/run/dbus/system_bus_socket \
                 -Dmodular_tests=enabled \
                 -Dchecks=true \
                 -Druntime_dir=${runtimedir} \
                 -Dtest_socket_dir=/tmp \
                 -Dsession_socket_dir=/tmp \
                "

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd x11', d)} \
                   user-session \
                  ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
                  "
PACKAGECONFIG:class-native = ""
PACKAGECONFIG:class-nativesdk = ""

PACKAGECONFIG[systemd] = "-Dsystemd=enabled -Dsystemd_system_unitdir=${systemd_system_unitdir},-Dsystemd=disabled,systemd"
PACKAGECONFIG[x11] = "-Dx11_autolaunch=enabled,-Dx11_autolaunch=disabled, virtual/libx11 libsm"
PACKAGECONFIG[user-session] = "-Duser_session=true -Dsystemd_user_unitdir=${systemd_user_unitdir},-Duser_session=false"
PACKAGECONFIG[verbose-mode] = "-Dverbose_mode=true,-Dverbose_mode=false,,"
PACKAGECONFIG[audit] = "-Dlibaudit=enabled,-Dlibaudit=disabled,audit"
PACKAGECONFIG[selinux] = "-Dselinux=enabled,-Dselinux=disabled,libselinux"
PACKAGECONFIG[tests] = "-Dinstalled_tests=true,-Dinstalled_tests=false"

DEPENDS = "expat virtual/libintl autoconf-archive-native glib-2.0"
RDEPENDS:${PN} += "${PN}-common ${PN}-tools"
RDEPENDS:${PN}:class-native = ""

inherit useradd update-rc.d

INITSCRIPT_NAME = "dbus-1"
INITSCRIPT_PARAMS = "start 02 5 3 2 . stop 20 0 1 6 ."

python __anonymous() {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}

PACKAGES =+ "${PN}-lib ${PN}-common ${PN}-tools"

USERADD_PACKAGES = "dbus-common"
USERADD_PARAM:dbus-common = "--system --home ${localstatedir}/lib/dbus \
                             --no-create-home --shell /bin/false \
                             --user-group messagebus"

CONFFILES:${PN} = "${sysconfdir}/dbus-1/system.conf ${sysconfdir}/dbus-1/session.conf"

DEBIANNAME:${PN} = "dbus-1"

OLDPKGNAME = "dbus-x11"
OLDPKGNAME:class-nativesdk = ""

# for compatibility
RPROVIDES:${PN} = "${OLDPKGNAME}"
RREPLACES:${PN} += "${OLDPKGNAME}"

FILES:${PN} = "${bindir}/dbus-daemon* \
               ${bindir}/dbus-cleanup-sockets \
               ${bindir}/dbus-launch \
               ${bindir}/dbus-run-session \
               ${libexecdir}/dbus* \
               ${sysconfdir} \
               ${localstatedir} \
               ${systemd_system_unitdir} \
               ${systemd_user_unitdir} \
               ${nonarch_libdir}/tmpfiles.d/dbus.conf \
"
FILES:${PN}-common = "${sysconfdir}/dbus-1 \
                      ${datadir}/dbus-1/services \
                      ${datadir}/dbus-1/system-services \
                      ${datadir}/dbus-1/session.d \
                      ${datadir}/dbus-1/session.conf \
                      ${datadir}/dbus-1/system.d \
                      ${datadir}/dbus-1/system.conf \
                      ${systemd_system_unitdir}/dbus.socket \
                      ${systemd_system_unitdir}/sockets.target.wants \
                      ${systemd_user_unitdir}/dbus.socket \
                      ${systemd_user_unitdir}/sockets.target.wants \
                      ${nonarch_libdir}/sysusers.d/dbus.conf \
"
FILES:${PN}-tools = "${bindir}/dbus-uuidgen \
                     ${bindir}/dbus-send \
                     ${bindir}/dbus-monitor \
                     ${bindir}/dbus-update-activation-environment \
"
FILES:${PN}-lib = "${libdir}/lib*.so.*"
RRECOMMENDS:${PN}-lib = "${PN}"
FILES:${PN}-dev += "${libdir}/dbus-1.0/include ${bindir}/dbus-test-tool ${datadir}/xml/dbus-1"

RDEPENDS:${PN}-ptest += "bash make dbus"

PACKAGE_WRITE_DEPS += "${@bb.utils.contains('DISTRO_FEATURES','systemd sysvinit','systemd-systemctl-native','',d)}"
pkg_postinst:dbus() {
	# If both systemd and sysvinit are enabled, mask the dbus-1 init script
        if ${@bb.utils.contains('DISTRO_FEATURES','systemd sysvinit','true','false',d)}; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		systemctl $OPTS mask dbus-1.service
	fi

	if [ -z "$D" ] && [ -e /etc/init.d/populate-volatile.sh ] ; then
		/etc/init.d/populate-volatile.sh update
	fi
}


do_install:append:class-target() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/init.d
		sed 's:@bindir@:${bindir}:' < ${UNPACKDIR}/dbus-1.init > ${S}/dbus-1.init.sh
		install -m 0755 ${S}/dbus-1.init.sh ${D}${sysconfdir}/init.d/dbus-1
		install -d ${D}${sysconfdir}/default/volatiles
		echo "d messagebus messagebus 0755 /run/dbus none" \
		     > ${D}${sysconfdir}/default/volatiles/99_dbus
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		for i in dbus.target.wants sockets.target.wants multi-user.target.wants; do \
			install -d ${D}${systemd_system_unitdir}/$i; done
		install -m 0644 ${B}/bus/dbus.service ${B}/bus/dbus.socket ${D}${systemd_system_unitdir}/
		ln -fs ../dbus.socket ${D}${systemd_system_unitdir}/dbus.target.wants/dbus.socket
		ln -fs ../dbus.socket ${D}${systemd_system_unitdir}/sockets.target.wants/dbus.socket
		ln -fs ../dbus.service ${D}${systemd_system_unitdir}/multi-user.target.wants/dbus.service
	fi


	mkdir -p ${D}${localstatedir}/lib/dbus

	chown messagebus:messagebus ${D}${localstatedir}/lib/dbus

	chown root:messagebus ${D}${libexecdir}/dbus-daemon-launch-helper
	chmod 4755 ${D}${libexecdir}/dbus-daemon-launch-helper

	# Remove Red Hat initscript
	rm -rf ${D}${sysconfdir}/rc.d

	# Remove empty testexec directory as we don't build tests
	rm -rf ${D}${libdir}/dbus-1.0/test

	# Remove /run as it is created on startup
	rm -rf ${D}${runtimedir}
}

do_install:append:class-native() {
	# dbus-launch has no X support so lets not install it in case the host
	# has a more featured and useful version
	rm -f ${D}${bindir}/dbus-launch
}

do_install:append:class-nativesdk() {
	# dbus-launch has no X support so lets not install it in case the host
	# has a more featured and useful version
	rm -f ${D}${bindir}/dbus-launch

	# Remove /run to avoid QA error
	rm -rf ${D}${runtimedir}
}
BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT += "d-bus_project:d-bus freedesktop:dbus freedesktop:libdbus"
