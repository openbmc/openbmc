SUMMARY = "Linux Bluetooth Stack Userland V5"
DESCRIPTION = "Linux Bluetooth stack V5 userland components.  These include a system configurations, daemons, tools and system libraries."
HOMEPAGE = "http://www.bluez.org"
SECTION = "libs"
LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
                    file://COPYING.LIB;md5=fb504b67c50331fc78734fed90fb0e09 \
                    file://src/main.c;beginline=1;endline=24;md5=9bc54b93cd7e17bf03f52513f39f926e"
DEPENDS = "udev libusb dbus-glib glib-2.0 libcheck readline"
PROVIDES += "bluez-hcidump"
RPROVIDES_${PN} += "bluez-hcidump"

RCONFLICTS_${PN} = "bluez4"

PACKAGECONFIG ??= "obex-profiles"
PACKAGECONFIG[obex-profiles] = "--enable-obex,--disable-obex,libical"
PACKAGECONFIG[experimental] = "--enable-experimental,--disable-experimental,"

SRC_URI = "\
    ${KERNELORG_MIRROR}/linux/bluetooth/bluez-${PV}.tar.xz \
    file://init \
    file://run-ptest \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '', 'file://0001-Allow-using-obexd-without-systemd-in-the-user-sessio.patch', d)} \
    file://0001-tests-add-a-target-for-building-tests-without-runnin.patch \
"
S = "${WORKDIR}/bluez-${PV}"

inherit autotools-brokensep pkgconfig systemd update-rc.d distro_features_check ptest

EXTRA_OECONF = "\
  --enable-tools \
  --disable-cups \
  --enable-test \
  --enable-datafiles \
  ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-systemd', '--disable-systemd', d)} \
  --enable-library \
"

# bluez5 builds a large number of useful utilities but does not
# install them.  Specify which ones we want put into ${PN}-noinst-tools.
NOINST_TOOLS_READLINE ??= ""
NOINST_TOOLS_EXPERIMENTAL ??= ""
NOINST_TOOLS = " \
    ${NOINST_TOOLS_READLINE} \
    ${@bb.utils.contains('PACKAGECONFIG', 'experimental', '${NOINST_TOOLS_EXPERIMENTAL}', '', d)} \
"

do_install_append() {
	install -d ${D}${INIT_D_DIR}
	install -m 0755 ${WORKDIR}/init ${D}${INIT_D_DIR}/bluetooth

	install -d ${D}${sysconfdir}/bluetooth/
	if [ -f ${S}/profiles/audio/audio.conf ]; then
	    install -m 0644 ${S}/profiles/audio/audio.conf ${D}/${sysconfdir}/bluetooth/
	fi
	if [ -f ${S}/profiles/network/network.conf ]; then
	    install -m 0644 ${S}/profiles/network/network.conf ${D}/${sysconfdir}/bluetooth/
	fi
	if [ -f ${S}/profiles/input/input.conf ]; then
	    install -m 0644 ${S}/profiles/input/input.conf ${D}/${sysconfdir}/bluetooth/
	fi

	# Install desired tools that upstream leaves in build area
        for f in ${NOINST_TOOLS} ; do
	    install -m 755 ${B}/$f ${D}/${bindir}
	done
}

ALLOW_EMPTY_libasound-module-bluez = "1"
PACKAGES =+ "libasound-module-bluez ${PN}-testtools ${PN}-obex ${PN}-noinst-tools"

FILES_libasound-module-bluez = "${libdir}/alsa-lib/lib*.so ${datadir}/alsa"
FILES_${PN} += "${libdir}/bluetooth/plugins/*.so ${base_libdir}/udev/ ${nonarch_base_libdir}/udev/ ${systemd_unitdir}/ ${datadir}/dbus-1"
FILES_${PN}-dev += "\
  ${libdir}/bluetooth/plugins/*.la \
  ${libdir}/alsa-lib/*.la \
"

FILES_${PN}-obex = "${libexecdir}/bluetooth/obexd \
                    ${exec_prefix}/lib/systemd/user/obex.service \
                    ${datadir}/dbus-1/services/org.bluez.obex.service \
                   "
SYSTEMD_SERVICE_${PN}-obex = "obex.service"

FILES_${PN}-testtools = "${libdir}/bluez/test/*"

def get_noinst_tools_paths (d, bb, tools):
    s = list()
    bindir = d.getVar("bindir", True)
    for bdp in tools.split():
        f = os.path.basename(bdp)
        s.append("%s/%s" % (bindir, f))
    return "\n".join(s)

FILES_${PN}-noinst-tools = "${@get_noinst_tools_paths(d, bb, d.getVar('NOINST_TOOLS', True))}"

RDEPENDS_${PN}-testtools += "python python-dbus python-pygobject"

SYSTEMD_SERVICE_${PN} = "bluetooth.service"
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "bluetooth"

EXCLUDE_FROM_WORLD = "1"

do_compile_ptest() {
        oe_runmake buildtests
}

do_install_ptest() {
        cp -r ${B}/unit/ ${D}${PTEST_PATH}
        rm ${D}${PTEST_PATH}/unit/*.c ${D}${PTEST_PATH}/unit/*.o
}
