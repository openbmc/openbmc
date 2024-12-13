SUMMARY  = "Wi-Fi Test Suite Linux Control Agent"
DESCRIPTION = "Wi-Fi Test Suite is a software platform originally developed \
by Wi-Fi Alliance, the global non-profit industry association that brings you \
Wi-Fi, to support certification program development and device certification."
HOMEPAGE = "https://www.wi-fi.org/certification/wi-fi-test-suite"
LICENSE  = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0542427ed5c315ca34aa09ae7a85ed32"
SECTION = "test"

S = "${WORKDIR}/git"
SRCREV = "12e85fbeca8ca21a632d18e55089a8a7606d64aa"
SRC_URI = "git://github.com/Wi-FiTestSuite/Wi-FiTestSuite-Linux-DUT.git;branch=master;protocol=https \
	file://0001-Use-toolchain-from-environment-variables.patch \
	file://0002-Add-missing-include-removes-unnedded-stuff-and-add-n.patch \
	file://0003-fix-path-to-usr-sbin-for-script-and-make-script-for-.patch \
	file://0004-run-ranlib-per-library-and-use-AR.patch \
        file://fno-common.patch \
        file://0001-wfa_cmdproc-Store-return-value-into-location.patch \
"

# to avoid host path QA error
CFLAGS += "-I${STAGING_INCDIR}/tirpc"
# Fix GNU HASH error
TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
	install -d ${D}${libdir}
	install -m 0644 ${S}/lib/libwfa.a ${D}${libdir}
	install -m 0644 ${S}/lib/libwfa_ca.a ${D}${libdir}
	install -m 0644 ${S}/lib/libwfa_dut.a ${D}${libdir}
	install -d ${D}${sbindir}
	install -m 0755 ${S}/dut/wfa_dut ${D}${sbindir}
	install -m 0755 ${S}/ca/wfa_ca ${D}${sbindir}
	install -m 0755 ${S}/scripts/*.sh ${D}${sbindir}
	install -m 0755 ${S}/scripts/arp_neigh_loop ${D}${sbindir}
	install -m 0755 ${S}/scripts/dev_send_frame ${D}${sbindir}
	install -m 0755 ${S}/scripts/sta_reset_parm ${D}${sbindir}
}

RDEPENDS:${PN} = "wpa-supplicant"

# http://errors.yoctoproject.org/Errors/Details/766893/
# wfa_cmdproc.c:467:68: error: passing argument 3 of 'strtok_r' from incompatible pointer type [-Wincompatible-pointer-types]
# wfa_cs.c:4175:57: error: initialization of 'caStaGetEventDetails_t *' {aka 'struct ca_sta_get_event_details *'} from incompatible pointer type 'caStaMngServ_t *' {aka 'struct ca_sta_manage_service *'} [-Wincompatible-pointer-types]
CC += "-Wno-error=incompatible-pointer-types"
