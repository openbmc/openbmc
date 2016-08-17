SUMMARY = "iSCSI daemon and utility programs"
DESCRIPTION = "Open-iSCSI project is a high performance, transport \
independent, multi-platform implementation of RFC3720. The iscsi package \
provides the server daemon for the iSCSI protocol, as well as the utility \
programs used to manage it. iSCSI is a protocol for distributed \
disk access using SCSI commands sent over Internet Protocol networks."
HOMEPAGE = "http://www.open-iscsi.org/"
LICENSE = "GPLv2 & LGPLv2.1"
SECTION = "net"
DEPENDS = "openssl flex-native bison-native"

LIC_FILES_CHKSUM = \
        "file://COPYING;md5=393a5ca445f6965873eca0259a17f833 \
         file://utils/open-isns/COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "http://www.open-iscsi.org/bits/open-iscsi-${PV}.tar.gz \
           file://iscsi-initiator-utils-use-var-for-config.patch \
           file://iscsi-initiator-utils-dont-use-static.patch \
           file://initd.debian \
           file://99_iscsi-initiator-utils \
           file://iscsi-initiator \
           file://iscsi-initiator.service \
           file://iscsi-initiator-targets.service \
"
SRC_URI[md5sum] = "8b8316d7c9469149a6cc6234478347f7"
SRC_URI[sha256sum] = "7dd9f2f97da417560349a8da44ea4fcfe98bfd5ef284240a2cc4ff8e88ac7cd9"

S = "${WORKDIR}/open-iscsi-${PV}"

inherit update-rc.d systemd

TARGET_CC_ARCH += "${LDFLAGS}"
do_configure () {
    #need to support cross-compiling in open-isns only
    (cd utils/open-isns; gnu-configize; \
     ./configure --host=${TARGET_SYS} --build=${BUILD_SYS} --with-security=no )
}

do_compile () {
    #make iscsistart one of PROGRAMS if install_user in do_install
    #sed -i -e '/^PROGRAMS = /s;$; usr/iscsistart;' Makefile

    #fix the ar used in open-isns
    sed -i -e 's:ar cr :$(AR) cr :' ${S}/utils/open-isns/Makefile
    oe_runmake user
}

do_install () {
    #completely override the install_user as bugs in Makefile
    #oe_runmake DESTDIR="${D}" install_user

    #install necessary directories
    install -d ${D}${sbindir} \
        ${D}${sysconfdir}/init.d \
        ${D}${sysconfdir}/iscsi \
        ${D}${localstatedir}/lib/iscsi/nodes \
        ${D}${localstatedir}/lib/iscsi/send_targets \
        ${D}${localstatedir}/lib/iscsi/static \
        ${D}${localstatedir}/lib/iscsi/isns \
        ${D}${localstatedir}/lib/iscsi/slp \
        ${D}${localstatedir}/lib/iscsi/ifaces \
        ${D}/${mandir}/man8

    install -p -m 755 ${S}/usr/iscsid ${S}/usr/iscsiadm \
        ${S}/utils/iscsi-iname \
        ${S}/usr/iscsistart ${D}/${sbindir}

    install -p -m 644 ${S}/doc/iscsiadm.8 ${S}/doc/iscsid.8 ${D}/${mandir}/man8
    install -p -m 644 ${S}/etc/iscsid.conf ${D}${sysconfdir}/iscsi
    install -p -m 755 ${WORKDIR}/initd.debian ${D}${sysconfdir}/init.d/iscsid

    sed -i -e "s:= /sbin/iscsid:= ${sbindir}/iscsid:" ${D}${sysconfdir}/iscsi/iscsid.conf

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/${BPN}/lock - - - -" \
                     > ${D}${sysconfdir}/tmpfiles.d/iscsi.conf
        install -d ${D}/etc/default/
        install -p -m 755 ${WORKDIR}/iscsi-initiator ${D}${sysconfdir}/default/

        install -d ${D}${systemd_unitdir}/system/
        install -m 0644 ${WORKDIR}/iscsi-initiator.service \
                        ${WORKDIR}/iscsi-initiator-targets.service \
                        ${D}${systemd_unitdir}/system/
    else
        install -d ${D}/etc/default/volatiles
        install -m 0644 ${WORKDIR}/99_iscsi-initiator-utils ${D}/etc/default/volatiles
    fi
}

pkg_postinst_${PN}() {
    #default there is no initiatorname.iscsi installed
    #but it is needed or iscsid will fail

    #will run only when postinst on target
    if [ "x$D" != "x" ]; then
        exit 1
    fi
    if [ ! -f ${sysconfdir}/iscsi/initiatorname.iscsi ]; then
        echo "InitiatorName=$(${sbindir}/iscsi-iname)" > \
        ${sysconfdir}/iscsi/initiatorname.iscsi
    fi

    if [ -e /etc/init.d/populate-volatile.sh ]; then
        /etc/init.d/populate-volatile.sh update
    elif command -v systemd-tmpfiles >/dev/null; then
        systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/iscsi.conf
    fi
}

SYSTEMD_SERVICE = " iscsi-initiator.service iscsi-initiator-targets.service "
INITSCRIPT_NAME = "iscsid"
INITSCRIPT_PARAMS = "start 30 1 2 3 4 5 . stop 70 0 1 2 3 4 5 6 ."
