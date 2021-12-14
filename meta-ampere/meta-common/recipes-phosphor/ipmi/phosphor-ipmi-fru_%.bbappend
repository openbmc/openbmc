FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

HOSTIPMI_PROVIDER_LIBRARY:remove = "libstrgfnhandler.so"

do_install:append () {
	rm -rf ${D}${libdir}/ipmid-providers
}
