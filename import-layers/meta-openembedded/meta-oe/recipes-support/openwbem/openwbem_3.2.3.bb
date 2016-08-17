SUMMARY = "Web Based Enterprise Management (WBEM) Implementation"
DESCRIPTION = "OpenWBEM is a set of software components that help facilitate \
deployment of the Common Information Model (CIM) and Web-Based \
Enterprise Management (WBEM) technologies of the Distributed Management \
Task Force (DMTF). \
 \
Web-Based Enterprise Management (WBEM) is a set of management and \
Internet standard technologies developed to unify the management of \
distributed computing environments. WBEM provides the ability for the \
industry to deliver a well-integrated set of standards-based management \
tools, facilitating the exchange of data across otherwise disparate \
technologies and platforms. \
 \
For more information about DMTF and its technologies, visit \
http://www.dmtf.org/standards. "
SECTION = "System/Management"
HOMEPAGE = "http://openwbem.sourceforge.net/"

inherit autotools-brokensep pkgconfig

SOURCE1="novell-openwbem-root-acl.mof"
SOURCE2="loadmof.sh"
SOURCE3="rmmof.sh"
SOURCE4="openwbem-owcimomd.init"
SOURCE5="openwbem-etc_pam.d_openwbem"
SOURCE6="openwbem-rpmlintrc"

SRC_URI = " \
   git://github.com/kkaempf/openwbem.git \
   file://${SOURCE1} \
   file://${SOURCE2} \
   file://${SOURCE3} \
   file://${SOURCE4} \
   file://${SOURCE5} \
   file://${SOURCE6} \
   file://checkserverkey \
   file://owcimomd.service \
"
SRCREV = "5c688eefc1f8e35a4b1c58529aae5f114c25c2a8"
S = "${WORKDIR}/git"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM += "file://COPYING;md5=0504a2eb85e01aa92c9efd4125a34660"
INSANE_SKIP_${PN} = "dev-so"
DEPENDS += "openssl libpam bash"
RDEPENDS_${PN} += "bash"
EXTRA_OECONF = " \
    --prefix=/usr \
    --sysconfdir=/etc \
    --libdir=${libdir} \
    --localstatedir=/var/lib \
    --libexecdir=${libdir}/openwbem/bin \
    --mandir=/usr/share/man \
    --enable-threads-run-as-user \
"
do_configure_prepend() {
    autoreconf --force --install
}

do_install() {
    oe_runmake DESTDIR=${D} install
    install -d ${D}/etc/openwbem/openwbem.conf.d
    install -d ${D}/var/adm/fillup-templates
    install -m 644 etc/sysconfig/daemons/owcimomd ${D}/var/adm/fillup-templates/sysconfig.owcimomd
    
    # fix up hardcoded paths
    sed -i -e 's,/usr/sbin/,${sbindir}/,' ${WORKDIR}/owcimomd.service
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/owcimomd.service ${D}/${systemd_unitdir}/system
        install -m 755 ${WORKDIR}/checkserverkey ${D}${sysconfdir}/openwbem/
    fi

    install -d ${D}/etc/init.d
    ln -sf ../../etc/init.d/owcimomd ${D}/usr/sbin/rcowcimomd
    install -m 755 ${WORKDIR}/${SOURCE4} ${D}/etc/init.d/owcimomd
    install -d ${D}${sbindir}
    install -d ${D}/usr/bin
    install -d ${D}/etc/pam.d
    install -d ${D}/${libdir}/openwbem/cmpiproviders
    install -m 644 etc/pam.d/openwbem ${D}/etc/pam.d
    install -d ${D}/${libdir}/openwbem/c++providers
    install -d ${D}/var/lib/openwbem
    install -m 755 ${WORKDIR}/${SOURCE2} ${D}/usr/bin/ow-loadmof.sh
    install -m 755 ${WORKDIR}/${SOURCE3} ${D}/usr/bin/ow-rmmof.sh
    install -m 644 ${WORKDIR}/${SOURCE5} ${D}/etc/pam.d/openwbem

    MOFPATH=${D}/usr/share/mof/openwbem
    install -d $MOFPATH
    mv ${D}/usr/share/openwbem/* $MOFPATH/
    rmdir ${D}/usr/share/openwbem
    install -m 644 ${WORKDIR}/${SOURCE1} $MOFPATH/

    touch ${D}/var/lib/openwbem/{classassociation,instances,instassociation,namespaces,schema}.{dat,ndx,lock}
}

inherit ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','systemd','systemd','', d)}
SYSTEMD_SERVICE_${PN} = "owcimomd.service"
SYSTEMD_AUTO_ENABLE = "disable"
FILES_${PN} += " \
    ${libdir} \
    ${datadir}/mof \
    ${systemd_unitdir} \
"
FILES_${PN}-dbg += " \
    ${libdir}/openwbem/c++providers/.debug \
    ${libdir}/openwbem/provifcs/.debug \
    ${libdir}/openwbem/bin/openwbem/.debug \
"
FILES_${PN}-dev = " \
    ${includedir} \
    ${datadir}/aclocal/openwbem.m4 \
"
