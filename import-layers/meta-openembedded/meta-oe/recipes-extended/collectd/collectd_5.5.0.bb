SUMMARY = "Collects and summarises system performance statistics"
DESCRIPTION = "collectd is a daemon which collects system performance statistics periodically and provides mechanisms to store the values in a variety of ways, for example in RRD files."
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1bd21f19f7f0c61a7be8ecacb0e28854"

DEPENDS = "rrdtool curl libpcap libxml2 yajl libgcrypt libtool lvm2"

SRC_URI = "http://collectd.org/files/collectd-${PV}.tar.bz2 \
           file://no-gcrypt-badpath.patch \
           file://collectd-version.patch \
           file://0001-redefine-the-dependence.patch  \
           file://collectd.init \
           file://collectd.service \
           file://0001-conditionally-check-libvirt.patch \
"
SRC_URI[md5sum] = "c39305ef5514b44238b0d31f77e29e6a"
SRC_URI[sha256sum] = "847684cf5c10de1dc34145078af3fcf6e0d168ba98c14f1343b1062a4b569e88"

inherit autotools pythonnative update-rc.d pkgconfig systemd

SYSTEMD_SERVICE_${PN} = "collectd.service"

# Floatingpoint layout, architecture dependent
# 'nothing', 'endianflip' or 'intswap'
FPLAYOUT ?= "--with-fp-layout=nothing"

PACKAGECONFIG ??= ""
PACKAGECONFIG[openjdk] = "--with-java=${STAGING_DIR_TARGET}${libdir}/jvm,--without-java,openjdk-7"
PACKAGECONFIG[snmp] = "--enable-snmp,--disable-snmp --with-libnetsnmp=no,net-snmp"
PACKAGECONFIG[libmemcached] = "--with-libmemcached,--without-libmemcached,libmemcached"
PACKAGECONFIG[iptables] = "--enable-iptables,--disable-iptables,iptables"
PACKAGECONFIG[postgresql] = "--enable-postgresql --with-libpq=yes, \
        --disable-postgresql --with-libpq=no,postgresql"
PACKAGECONFIG[mysql] = "--enable-mysql --with-libmysql=yes, \
        --disable-mysql --with-libmysql=no,mysql5"
PACKAGECONFIG[dbi] = "--enable-dbi,--disable-dbi,libdbi"
PACKAGECONFIG[modbus] = "--enable-modbus,--disable-modbus,libmodbus"
PACKAGECONFIG[libowcapi] = "--with-libowcapi,--without-libowcapi,owfs"
PACKAGECONFIG[sensors] = "--enable-sensors --with-libsensors=yes, \
        --disable-sensors --with-libsensors=no,lmsensors"
PACKAGECONFIG[amqp] = "--enable-amqp --with-librabbitmq=yes, \
        --disable-amqp --with-librabbitmq=no,rabbitmq-c"
# protobuf-c, libvirt that are currently only available in meta-virtualization layer
PACKAGECONFIG[pinba] = "--enable-pinba,--disable-pinba,protobuf-c-native protobuf-c"
PACKAGECONFIG[libvirt] = "--enable-libvirt,--disable-libvirt,libvirt"
PACKAGECONFIG[libesmtp] = "--with-libesmtp,--without-libesmtp,libesmtp"
PACKAGECONFIG[libmnl] = "--with-libmnl,--without-libmnl,libmnl"
PACKAGECONFIG[libatasmart] = "--with-libatasmart,--without-libatasmart,libatasmart"
PACKAGECONFIG[ldap] = "--enable-openldap --with-libldap,--disable-openldap --without-libldap, openldap"

EXTRA_OECONF = " \
                ${FPLAYOUT} \
                --disable-perl --with-libperl=no --with-perl-bindings=no \
                --with-libgcrypt=${STAGING_BINDIR_CROSS}/libgcrypt-config \
                --disable-notify_desktop \
"

do_install_append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/collectd.init ${D}${sysconfdir}/init.d/collectd
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/collectd
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/collectd
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/collectd
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/collectd

    # Fix configuration file to allow collectd to start up
    sed -i 's!^#FQDNLookup[ \t]*true!FQDNLookup   false!g' ${D}${sysconfdir}/collectd.conf

    rmdir "${D}${localstatedir}/run"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"

    # Install systemd unit files
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/collectd.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        ${D}${systemd_unitdir}/system/collectd.service
}

INITSCRIPT_NAME = "collectd"
INITSCRIPT_PARAMS = "defaults"

# threshold.so load.so are also provided by gegl
# disk.so is also provided by libgphoto2-camlibs
PRIVATE_LIBS = "threshold.so load.so disk.so"
