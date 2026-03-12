SUMMARY = "An open source remote desktop protocol(rdp) server."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=72cfbe4e7bd33a0a1de9630c91195c21 \
"

inherit features_check autotools pkgconfig useradd systemd ptest

DEPENDS = "openssl virtual/libx11 libxfixes libxrandr libpam nasm-native imlib2 pixman libsm"

REQUIRED_DISTRO_FEATURES = "x11 pam"

SRC_URI = "https://github.com/neutrinolabs/${BPN}/releases/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://xrdp.sysconfig \
           file://run-ptest \
           file://0001-Added-req_distinguished_name-in-etc-xrdp-openssl.con.patch \
           file://0001-arch-Define-NO_NEED_ALIGN-on-ppc64.patch \
           file://0001-mark-count-with-unused-attribute.patch \
           "
SRC_URI[sha256sum] = "9abc96d164de4b1c40e2f3f537d0593d052a640cf3388978c133715ea69fb123"

UPSTREAM_CHECK_URI = "https://github.com/neutrinolabs/xrdp/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

CFLAGS += " -Wno-deprecated-declarations"

PACKAGECONFIG ??= "fuse ${@bb.utils.contains('PTEST_ENABLED', '1', 'test', '', d)}"
PACKAGECONFIG[fuse] = " --enable-fuse, --disable-fuse, fuse3"
PACKAGECONFIG[test] = " --enable-tests, , libcheck cmocka"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system xrdp"
USERADD_PARAM:${PN}  = "--system --home /var/run/xrdp -g xrdp \
                        --no-create-home --shell /bin/false xrdp"

FILES:${PN} += "${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/accessibility-services/*.service "

FILES:${PN}-dev += " \
    ${libdir}/xrdp/libcommon.so \
    ${libdir}/xrdp/libxrdp.so \
    ${libdir}/xrdp/libxrdpapi.so \
    ${libdir}/xrdp/libtoml.so \
    ${libdir}/xrdp/libsesman.so \
    ${libdir}/xrdp/libipm.so \
    "

EXTRA_OECONF = "--enable-pam-config=suse --enable-fuse \
                --enable-pixman --enable-painter --enable-vsock \
                --enable-ipv6 --with-imlib2 --with-socketdir=${localstatedir}/run/${PN}"

do_configure:prepend() {
    cd ${S}
    ./bootstrap
    cd -
}

do_compile:prepend() {
    sed -i 's/(MAKE) $(AM_MAKEFLAGS) install-exec-am install-data-am/(MAKE) $(AM_MAKEFLAGS) install-exec-am/g' ${S}/keygen/Makefile.in
    echo "" > ${B}/xrdp_configure_options.h
}

do_install:append() {

	# deal with systemd unit files
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/instfiles/xrdp.service.in ${D}${systemd_unitdir}/system/xrdp.service
	install -m 0644 ${S}/instfiles/xrdp-sesman.service.in ${D}${systemd_unitdir}/system/xrdp-sesman.service
	sed -i -e 's,@localstatedir@,${localstatedir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service
	sed -i -e 's,@sysconfdir@,${sysconfdir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service
	sed -i -e 's,@sbindir@,${sbindir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service

	install -d ${D}${sysconfdir}/sysconfig/xrdp
	install -m 0644 ${S}/instfiles/*.ini ${D}${sysconfdir}/xrdp/
	install -m 0644 ${S}/keygen/openssl.conf ${D}${sysconfdir}/xrdp/
	install -m 0644 ${UNPACKDIR}/xrdp.sysconfig ${D}${sysconfdir}/sysconfig/xrdp/
	chown xrdp:xrdp ${D}${sysconfdir}/xrdp
}

do_compile_ptest() {
	for testdir in $(find ./tests -type d -mindepth 1); do
		cd $testdir
		echo 'buildtest-TESTS: $(check_PROGRAMS)' >> Makefile
		# change the test-data folder to ./data instead of ${S}
		sed -i 's|-D TOP_SRCDIR=[^ ]*|-D TOP_SRCDIR=\\"./data\\"|' Makefile
		# another test data folder redirection
		sed -i 's|-D IMAGEDIR=[^ ]*|-D IMAGEDIR=\\"./data\\"|' Makefile
		# and another
		sed -i 's|-DXRDP_TOP_SRCDIR=[^ ]*|-DXRDP_TOP_SRCDIR=\\"..\\"|' Makefile
		oe_runmake buildtest-TESTS
		cd -
	done
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests/xrdp/gfx
	install -d ${D}${PTEST_PATH}/tests/data/xrdp
	for testbin in $(find ./tests -type f -executable -mindepth 3); do
		install $testbin ${D}${PTEST_PATH}/tests/
	done
	install -m 666 ${S}/xrdp/xrdp256.bmp ${D}${PTEST_PATH}/tests/data/xrdp/
	install -m 666 ${S}/xrdp/ad256.bmp ${D}${PTEST_PATH}/tests/data/xrdp/
	install -m 666 ${S}/tests/xrdp/*.bmp ${D}${PTEST_PATH}/tests/data/
	install -m 666 ${S}/tests/xrdp/test1.jpg ${D}${PTEST_PATH}/tests/data/
	install -m 666 ${S}/tests/xrdp/test_alpha_blend.png ${D}${PTEST_PATH}/tests/data/
	install -m 666 ${S}/tests/xrdp/gfx/* ${D}${PTEST_PATH}/tests/xrdp/gfx/
}

RDEPENDS:${PN}-ptest += "imlib2-loaders"

SYSTEMD_SERVICE:${PN} = "xrdp.service xrdp-sesman.service"

pkg_postinst:${PN}() {
	if test -z "$D"
	then
		if test -x ${bindir}/xrdp-keygen
		then
			${bindir}/xrdp-keygen xrdp ${sysconfdir}/xrdp/rsakeys.ini >/dev/null
                fi
		if test ! -s ${sysconfdir}/xrdp/cert.pem
		then
			openssl req -x509 -newkey rsa:2048 -sha256 -nodes -days 3652 \
			-keyout ${sysconfdir}/xrdp/key.pem \
			-out ${sysconfdir}/xrdp/cert.pem \
			-config ${sysconfdir}/xrdp/openssl.conf >/dev/null 2>&1
			chmod 400 ${sysconfdir}/xrdp/key.pem
		fi
        fi
}
