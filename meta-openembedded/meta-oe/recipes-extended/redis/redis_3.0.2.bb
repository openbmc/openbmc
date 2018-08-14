SUMMARY = "Redis key-value store"
DESCRIPTION = "Redis is an open source, advanced key-value store."
HOMEPAGE = "http://redis.io"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3c01b49fed4df1a79843688fa3f7b9d6"
DEPENDS = ""

SRC_URI = "http://download.redis.io/releases/${BP}.tar.gz \
           file://hiredis-use-default-CC-if-it-is-set.patch \
           file://lua-update-Makefile-to-use-environment-build-setting.patch \
           file://oe-use-libc-malloc.patch \
           file://redis.conf \
           file://init-redis-server \
           file://redis.service \
           file://hiredis-update-Makefile-to-add-symbols-to-staticlib.patch \
"

SRC_URI[md5sum] = "87be8867447f62524b584813e5a7bd14"
SRC_URI[sha256sum] = "93e422c0d584623601f89b956045be158889ebe594478a2c24e1bf218495633f"

inherit autotools-brokensep update-rc.d systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN}  = "--system --home-dir /var/lib/redis -g redis --shell /bin/false redis"
GROUPADD_PARAM_${PN} = "--system redis"


REDIS_ON_SYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}"

do_install() {
    export PREFIX=${D}/${prefix}
    oe_runmake install
    install -d ${D}/${sysconfdir}/redis
    install -m 0644 ${WORKDIR}/redis.conf ${D}/${sysconfdir}/redis/redis.conf
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/init-redis-server ${D}/${sysconfdir}/init.d/redis-server
    install -d ${D}/var/lib/redis/
    chown redis.redis ${D}/var/lib/redis/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/redis.service ${D}${systemd_system_unitdir}
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${systemd_system_unitdir}/redis.service

    if [ "${REDIS_ON_SYSTEMD}" = true ]; then
        sed -i 's!daemonize yes!# daemonize yes!' ${D}/${sysconfdir}/redis/redis.conf
    fi
}

CONFFILES_${PN} = "${sysconfdir}/redis/redis.conf"

INITSCRIPT_NAME = "redis-server"
INITSCRIPT_PARAMS = "defaults 87"

SYSTEMD_SERVICE_${PN} = "redis.service"
