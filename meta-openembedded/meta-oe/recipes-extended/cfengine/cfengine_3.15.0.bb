#
# Copyright (C) 2014 - 2017 Wind River Systems, Inc.
#
SUMMARY = "CFEngine is an IT infrastructure automation framework"

DESCRIPTION = "CFEngine is an IT infrastructure automation framework \
that helps engineers, system administrators and other stakeholders \
in an IT system to manage and understand IT infrastructure throughout \
its lifecycle. CFEngine takes systems from Build to Deploy, Manage and Audit."

HOMEPAGE = "http://cfengine.com"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=233aa25e53983237cf0bd4c238af255f"

DEPENDS = "attr tokyocabinet bison-native"

SRC_URI = "https://cfengine-package-repos.s3.amazonaws.com/tarballs/${BP}.tar.gz \
           file://set-path-of-default-config-file.patch \
           "
SRC_URI[md5sum] = "d4dabfa46d8afa151be5610f184354e7"
SRC_URI[sha256sum] = "fa53e137f850eb268a8e7ae4578b5db5dc383656341f5053dc1a353ed0288265"

inherit autotools-brokensep systemd

export EXPLICIT_VERSION="${PV}"

SYSTEMD_SERVICE_${PN} = "cfengine3.service cf-apache.service cf-hub.service cf-postgres.service \
                         cf-runalerts.service cf-execd.service \
                         cf-monitord.service  cf-serverd.service \
"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

PACKAGECONFIG ??= "libpcre openssl \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd', d)} \
"
PACKAGECONFIG[libxml2] = "--with-libxml2=yes,--with-libxml2=no,libxml2,"
PACKAGECONFIG[mysql] = "--with-mysql=yes,--with-mysql=no,mysql,"
PACKAGECONFIG[postgresql] = "--with-postgresql=yes,--with-postgresql=no,postgresql,"
PACKAGECONFIG[acl] = "--with-libacl=yes,--with-libacl=no,acl,"
PACKAGECONFIG[libvirt] = "--with-libvirt=yes,--with-libvirt=no,libvirt,"
PACKAGECONFIG[libpcre] = "--with-pcre=yes,--with-pcre=no,libpcre,"
PACKAGECONFIG[openssl] = "--with-openssl=yes,--with-openssl=no,openssl,"
PACKAGECONFIG[pam] = "--with-pam=yes,--with-pam=no,libpam,"
PACKAGECONFIG[libyaml] = "--with-libyaml,--without-libyaml,libyaml,"
PACKAGECONFIG[systemd] = "--with-systemd-service=${systemd_system_unitdir},--without-systemd-service"
PACKAGECONFIG[libcurl] = "--with-libcurl,--without-libcurl,curl,"

EXTRA_OECONF = "hw_cv_func_va_copy=yes --with-init-script=${sysconfdir}/init.d --with-tokyocabinet"

do_install_append() {
    install -d ${D}${localstatedir}/${BPN}/bin
    for f in `ls ${D}${bindir}`; do
        ln -s ${bindir}/`basename $f` ${D}${localstatedir}/${BPN}/bin/
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

RDEPENDS_${PN} += "${BPN}-masterfiles"
