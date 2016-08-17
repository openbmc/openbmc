DESCRIPTION = "iSCSI Enterprise Target is aimed to develop an \
               open source iSCSI target with professional features, \
               that works well in enterprise environment under real \
               workload, and is scalable and versatile enough to meet the \
               challenge of future storage needs and developments."
HOMEPAGE = "http://iscsitarget.sourceforge.net/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6e233eda45c807aa29aeaa6d94bc48a2"
DEPENDS = "openssl virtual/kernel"

SRC_URI = "http://ftp.heanet.ie/mirrors/ubuntu/pool/universe/i/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://use-kernel-makefile-to-get-kernel-version.patch \
           file://fix-errors-observed-with-linux-3.19-and-greater.patch \
           file://access-sk_v6_daddr-iff-IPV6-defined.patch \
           file://build_with_updated_bio_struct_of_linux_v4.3_and_above.patch"

SRC_URI[md5sum] = "ef9bc823bbabd3c772208c00d5f2d089"
SRC_URI[sha256sum] = "d3196ccb78a43266dce28587bfe30d8ab4db7566d7bce96057dfbb84100babb5"

inherit module

do_configure[noexec] = "1"

# make_scripts requires kernel source directory to create
# kernel scripts
do_make_scripts[depends] += "virtual/kernel:do_shared_workdir"

do_compile() {
    oe_runmake KSRC=${STAGING_KERNEL_DIR} LDFLAGS='' V=1
}

do_install() {
    # Module
    install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/iscsi
    install -m 0644 kernel/iscsi_trgt.ko \
    ${D}/lib/modules/${KERNEL_VERSION}/kernel/iscsi/iscsi_trgt.ko
    
    # Userspace utilities
    install -d ${D}${sbindir}
    install -m 0755 usr/ietd ${D}${sbindir}/ietd
    install -m 0755 usr/ietadm ${D}${sbindir}/ietadm
    
    # Config files, init scripts
    mkdir -p ${D}${sysconfdir}/iet
    install -m 0644 etc/ietd.conf ${D}/${sysconfdir}/iet/ietd.conf
    install -m 0644 etc/initiators.allow ${D}${sysconfdir}/iet/initiators.allow
    install -m 0644 etc/targets.allow ${D}${sysconfdir}/iet/targets.allow
    mkdir -p ${D}${sysconfdir}/init.d
    install -m 0755 etc/initd/initd ${D}${sysconfdir}/init.d/iscsi-target
    install -m 0644 etc/initiators.deny ${D}${sysconfdir}/iet/initiators.deny
}

FILES_${PN} += "${sbindir} \
                ${sysconfdir}"

RDEPENDS_${PN} = "kernel-module-iscsi-trgt"
RRECOMMENDS_${PN} = "kernel-module-crc32c kernel-module-libcrc32c"
