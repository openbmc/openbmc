#
# Copyright (C) 2014 - 2017 Wind River Systems, Inc.
#
SUMMARY = "CFEngine is an IT infrastructure automation framework"

DESCRIPTION = "CFEngine is an IT infrastructure automation framework \
that helps engineers, system administrators and other stakeholders \
in an IT system to manage and understand IT infrastructure throughout \
its lifecycle. CFEngine takes systems from Build to Deploy, Manage and Audit."

HOMEPAGE = "http://cfengine.com"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eef43e6a0b5a8f46ef7f11e1e4b32a6c"

DEPENDS += "attr tokyocabinet bison-native openssl libpcre2 librsync"

SRC_URI = "https://cfengine-package-repos.s3.amazonaws.com/tarballs/${BPN}-community-${PV}.tar.gz \
           file://set-path-of-default-config-file.patch \
           "
SRC_URI[sha256sum] = "d3c3884b314dae48a6884e919d0a12acac5aea95d970544e4632a1773857d19b"

inherit autotools-brokensep systemd

export EXPLICIT_VERSION = "${PV}"

SYSTEMD_SERVICE:${PN} = "cfengine3.service cf-apache.service cf-hub.service cf-postgres.service \
                         cf-execd.service cf-php-fpm.service \
                         cf-monitord.service  cf-serverd.service \
                         cf-reactor.service \
"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd', d)}"

PACKAGECONFIG[libxml2] = "--with-libxml2=yes,--with-libxml2=no,libxml2,"
PACKAGECONFIG[mysql] = "--with-mysql=yes,--with-mysql=no,mariadb,"
PACKAGECONFIG[postgresql] = "--with-postgresql=yes,--with-postgresql=no,postgresql,"
PACKAGECONFIG[acl] = "--with-libacl=yes,--with-libacl=no,acl,"
PACKAGECONFIG[libvirt] = "--with-libvirt=yes,--with-libvirt=no,libvirt,"
PACKAGECONFIG[pam] = "--with-pam=yes,--with-pam=no,libpam,"
PACKAGECONFIG[libyaml] = "--with-libyaml,--without-libyaml,libyaml,"
PACKAGECONFIG[systemd] = "--with-systemd-service=${systemd_system_unitdir},--without-systemd-service"
PACKAGECONFIG[libcurl] = "--with-libcurl,--without-libcurl,curl,"

EXTRA_OECONF = "hw_cv_func_va_copy=yes --with-init-script=${sysconfdir}/init.d --with-tokyocabinet"
CFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'mysql', '-I${STAGING_INCDIR}/mysql', '', d)}"
CFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'libxml2', '-I${STAGING_INCDIR}/libxml2', '', d)}"

do_install:append() {
    install -d ${D}${localstatedir}/${BPN}/bin
    for f in $(find ${D}${bindir} -type f); do
         ln -sr $f ${D}${localstatedir}/${BPN}/bin/
    done

    install -d ${D}${sysconfdir}/default
    cat << EOF > ${D}${sysconfdir}/default/cfengine3
RUN_CF_SERVERD=1
RUN_CF_EXECD=1
RUN_CF_MONITORD=1
RUN_CF_HUB=0
EOF

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0755 -D ${D}${sysconfdir}/init.d/cfengine3 ${D}${datadir}/${BPN}/cfengine3
        sed -i -e 's#/etc/init.d#${datadir}/${BPN}#' ${D}${systemd_system_unitdir}/*.service
    fi
    rm -rf ${D}${datadir}/cfengine/modules/packages/zypper
}

RDEPENDS:${PN} += "${BPN}-masterfiles"

FILES:${PN} += "${libdir}/python"
