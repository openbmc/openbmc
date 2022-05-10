SUMMARY = "OPTEE Client"
HOMEPAGE = "https://github.com/OP-TEE/optee_client"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=69663ab153298557a59c67a60a743e5b"

PV = "3.16.0+git${SRCPV}"

inherit python3native systemd

SRC_URI = "git://github.com/OP-TEE/optee_client.git;branch=master;protocol=https \
           file://tee-supplicant.service \
    "
SRCREV = "06db73b3f3fdb8d23eceaedbc46c49c0b45fd1e2"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CFG_TEE_FS_PARENT_PATH=/var/tee \
        "

do_install() {
    oe_runmake install

    install -D -p -m0755 ${S}/out/export/usr/sbin/tee-supplicant ${D}${bindir}/tee-supplicant

    install -D -p -m0644 ${S}/out/export/usr/lib/libteec.so.1.0.0 ${D}${libdir}/libteec.so.1.0.0
    ln -sf libteec.so.1.0.0 ${D}${libdir}/libteec.so
    ln -sf libteec.so.1.0.0 ${D}${libdir}/libteec.so.1
    ln -sf libteec.so.1.0.0 ${D}${libdir}/libteec.so.1.0

    cp -a ${S}/out/export/usr/include ${D}/usr/

	install -d ${D}${systemd_system_unitdir}/
	install -m 0644 ${WORKDIR}/tee-supplicant.service ${D}${systemd_system_unitdir}/
	sed -i -e s:/etc:${sysconfdir}:g -e s:/usr/bin:${bindir}:g ${D}${systemd_system_unitdir}/tee-supplicant.service
}

SYSTEMD_SERVICE:${PN} = "tee-supplicant.service"

FILES:${PN} += "${libdir}/* ${includedir}/* ${systemd_system_unitdir}/*"
