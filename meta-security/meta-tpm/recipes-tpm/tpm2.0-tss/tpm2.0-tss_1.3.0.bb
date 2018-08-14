SUMMARY = "Software stack for TPM2."
DESCRIPTION = "tpm2.0-tss like woah."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"
SECTION = "tpm"

DEPENDS = "autoconf-archive pkgconfig"

SRCREV = "b1d9ece8c6bea2e3043943b2edfaebcdca330c38"

SRC_URI = " \
    git://github.com/tpm2-software/tpm2-tss.git;branch=1.x \
    file://ax_pthread.m4 \
"

inherit autotools pkgconfig systemd

S = "${WORKDIR}/git"

do_configure_prepend () {
	mkdir -p ${S}/m4
	cp ${WORKDIR}/ax_pthread.m4 ${S}/m4
	# execute the bootstrap script
	currentdir=$(pwd)
	cd ${S}
	ACLOCAL="aclocal --system-acdir=${STAGING_DATADIR}/aclocal" ./bootstrap
	cd $currentdir
}

INHERIT += "extrausers"
EXTRA_USERS_PARAMS = "\
	useradd -p '' tss; \
	groupadd tss; \
	"

SYSTEMD_PACKAGES = "resourcemgr"
SYSTEMD_SERVICE_resourcemgr = "resourcemgr.service"
SYSTEMD_AUTO_ENABLE_resourcemgr = "enable"

do_patch[postfuncs] += "${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','systemd','fix_systemd_unit','', d)}"
fix_systemd_unit () {
    sed -i -e 's;^ExecStart=.*/resourcemgr;ExecStart=${sbindir}/resourcemgr;' ${S}/contrib/resourcemgr.service
}

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m0644 ${S}/contrib/resourcemgr.service ${D}${systemd_system_unitdir}/resourcemgr.service
    fi
}

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN}-dbg \
    ${PN}-doc \
    libtss2 \
    libtss2-dev \
    libtss2-staticdev \
    libtctidevice \
    libtctidevice-dev \
    libtctidevice-staticdev \
    libtctisocket \
    libtctisocket-dev \
    libtctisocket-staticdev \
    resourcemgr \
"

FILES_libtss2 = " \
	${libdir}/libsapi.so.0.0.0 \
	${libdir}/libmarshal.so.0.0.0 \
"
FILES_libtss2-dev = " \
    ${includedir}/sapi \
    ${includedir}/tcti/common.h \
    ${libdir}/libsapi.so* \
    ${libdir}/libmarshal.so* \
    ${libdir}/pkgconfig/sapi.pc \
"
FILES_libtss2-staticdev = " \
    ${libdir}/libsapi.a \
    ${libdir}/libsapi.la \
    ${libdir}/libmarshal.a \
    ${libdir}/libmarshal.la \
"
FILES_libtctidevice = "${libdir}/libtcti-device.so.0.0.0"
FILES_libtctidevice-dev = " \
    ${includedir}/tcti/tcti_device.h \
    ${libdir}/libtcti-device.so* \
    ${libdir}/pkgconfig/tcti-device.pc \
"
FILES_libtctidevice-staticdev = "${libdir}/libtcti-device.*a"
FILES_libtctisocket = "${libdir}/libtcti-socket.so.0.0.0"
FILES_libtctisocket-dev = " \
    ${includedir}/tcti/tcti_socket.h \
    ${libdir}/libtcti-socket.so* \
    ${libdir}/pkgconfig/tcti-socket.pc \
"
FILES_libtctisocket-staticdev = "${libdir}/libtcti-socket.*a"
FILES_resourcemgr = "${sbindir}/resourcemgr ${systemd_system_unitdir}/resourcemgr.service"
