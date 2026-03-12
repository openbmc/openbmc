SUMMARY = "Redis key-value store"
DESCRIPTION = "Redis is an open source, advanced key-value store."
HOMEPAGE = "http://redis.io"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4feb9943a139a170b9d6ed6ec3342d0c"
DEPENDS = "readline lua ncurses"

SRC_URI = "http://download.redis.io/releases/${BP}.tar.gz \
           file://redis.conf \
           file://init-redis-server \
           file://redis.service \
           file://0001-hiredis-use-default-CC-if-it-is-set.patch \
           file://0002-lua-update-Makefile-to-use-environment-build-setting.patch \
           file://0003-hack-to-force-use-of-libc-malloc.patch \
           file://0004-src-Do-not-reset-FINAL_LIBS.patch \
           file://0005-Define-_GNU_SOURCE-to-get-PTHREAD_MUTEX_INITIALIZER.patch \
          "
SRC_URI[sha256sum] = "cf395665ba5fcecc4ef7aed1d8ab19c268619d98595827565c82344160171262"

RPROVIDES:${PN} = "virtual-redis"

CVE_STATUS[CVE-2025-21605] = "cpe-incorrect: the used version already contains the fix"
CVE_STATUS[CVE-2025-27151] = "cpe-incorrect: the used version already contains the fix"
CVE_STATUS[CVE-2025-46686] = "disputed: upstream rejected because mitigating it would affect other functionality"
CVE_STATUS[CVE-2025-46817] = "cpe-incorrect: the used version already contains the fix"
CVE_STATUS[CVE-2025-46818] = "cpe-incorrect: the used version already contains the fix"
CVE_STATUS[CVE-2025-46819] = "cpe-incorrect: the used version already contains the fix"

inherit pkgconfig update-rc.d systemd useradd

FINAL_LIBS:x86:toolchain-clang = "-latomic"
FINAL_LIBS:riscv32 = "-latomic"
FINAL_LIBS:mips = "-latomic"
FINAL_LIBS:arm = "-latomic"
FINAL_LIBS:powerpc = "-latomic"

export FINAL_LIBS

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN}  = "--system --home-dir /var/lib/redis -g redis --shell /bin/false redis"
GROUPADD_PARAM:${PN} = "--system redis"

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "USE_SYSTEMD=yes,USE_SYSTEMD=no,systemd"

EXTRA_OEMAKE += "${PACKAGECONFIG_CONFARGS}"

TARGET_LDFLAGS:append = " ${DEBUG_PREFIX_MAP}"

do_compile:prepend() {
    oe_runmake -C deps hdr_histogram fpconv hiredis lua linenoise
}

do_install() {
    export PREFIX=${D}/${prefix}
    oe_runmake install
    install -d ${D}/${sysconfdir}/redis
    install -m 0644 ${UNPACKDIR}/redis.conf ${D}/${sysconfdir}/redis/redis.conf
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/init-redis-server ${D}/${sysconfdir}/init.d/redis-server
    install -d ${D}/var/lib/redis/
    chown redis.redis ${D}/var/lib/redis/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/redis.service ${D}${systemd_system_unitdir}
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${systemd_system_unitdir}/redis.service

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        sed -i 's!daemonize yes!# daemonize yes!' ${D}/${sysconfdir}/redis/redis.conf
        sed -i 's!supervised no!supervised systemd!' ${D}/${sysconfdir}/redis/redis.conf
    fi
}

CONFFILES:${PN} = "${sysconfdir}/redis/redis.conf"

INITSCRIPT_NAME = "redis-server"
INITSCRIPT_PARAMS = "defaults 87"

SYSTEMD_SERVICE:${PN} = "redis.service"

CVE_STATUS[CVE-2022-3734] = "not-applicable-platform: CVE only applies for Windows."
# The vulnerability originates from Debian's packaging methodology,
# which loads system-wide Lua libraries (lua-cjson, lua-cmsgpack),
# enabling Lua sandbox escape. Upstream Redis builds, including
# those built by Yocto/OpenEmbedded, utilize embedded Lua from the
# deps/ directory and are therefore not affected by this issue.
CVE_STATUS[CVE-2022-0543] = "not-applicable-config: Debian-specific packaging issue caused by loading system-wide Lua libraries; upstream builds use embedded Lua and are not affected"
