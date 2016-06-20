SUMMARY = "Random number generator daemon"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0b6f033afe6db235e559456585dc8cdc"

SRC_URI = "http://heanet.dl.sourceforge.net/sourceforge/gkernel/${BP}.tar.gz \
           file://0001-If-the-libc-is-lacking-argp-use-libargp.patch \
           file://0002-Add-argument-to-control-the-libargp-dependency.patch \
           file://underquote.patch \
           file://uclibc-libuargp-configure.patch \
           file://init \
           file://default"

SRC_URI[md5sum] = "6726cdc6fae1f5122463f24ae980dd68"
SRC_URI[sha256sum] = "60a102b6603bbcce2da341470cad42eeaa9564a16b4490e7867026ca11a3078e"

# As the recipe doesn't inherit systemd.bbclass, we need to set this variable
# manually to avoid unnecessary postinst/preinst generated.
python () {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}

inherit autotools update-rc.d

PACKAGECONFIG = "libgcrypt"
PACKAGECONFIG_libc-musl = "libargp"
PACKAGECONFIG_libc-uclibc = "libuargp"
PACKAGECONFIG[libargp] = "--with-libargp,--without-libargp,argp-standalone,"
PACKAGECONFIG[libuargp] = "--enable-uclibc,,,"
PACKAGECONFIG[libgcrypt] = "--with-libgcrypt,--without-libgcrypt,libgcrypt,"

do_install_append() {
    # Only install the init script when 'sysvinit' is in DISTRO_FEATURES.
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d "${D}${sysconfdir}/init.d"
        install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/rng-tools
        sed -i -e 's,/etc/,${sysconfdir}/,' -e 's,/usr/sbin/,${sbindir}/,' \
            ${D}${sysconfdir}/init.d/rng-tools

        install -d "${D}${sysconfdir}/default"
        install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/rng-tools
    fi
}

INITSCRIPT_NAME = "rng-tools"
INITSCRIPT_PARAMS = "start 30 2 3 4 5 . stop 30 0 6 1 ."
