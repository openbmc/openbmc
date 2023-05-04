SUMMARY = "D-Bus test package (for D-bus functionality testing only)"
HOMEPAGE = "http://dbus.freedesktop.org"
SECTION = "base"

require dbus.inc

SRC_URI += "file://run-ptest \
            file://python-config.patch \
	    "

DEPENDS = "dbus glib-2.0"

RDEPENDS_${PN}-dev = ""

S="${WORKDIR}/dbus-${PV}"
FILESEXTRAPATHS =. "${FILE_DIRNAME}/dbus:"

inherit ptest

EXTRA_OECONF += "--enable-tests \
                --enable-modular-tests \
                --enable-installed-tests \
                --enable-checks \
                --enable-asserts \
                --with-dbus-test-dir=${PTEST_PATH} \
                --enable-embedded-tests \
             "

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
