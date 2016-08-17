SUMMARY = "User support binary for the uvesafb kernel module"
HOMEPAGE = "http://dev.gentoo.org/~spock/projects/uvesafb/"

# the copyright info is at the bottom of README, expect break
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README;md5=94ac1971e4f2309dc322d598e7b1f7dd"

DEPENDS = "virtual/kernel"
RRECOMMENDS_${PN} = "kernel-module-uvesafb"
PR = "r2"

SRC_URI = "http://distfiles.gentoo.org/distfiles/${BP}.tar.bz2 \
           file://Update-x86emu-from-X.org.patch \
           file://fbsetup \
           file://uvesafb.conf \
           file://ar-from-env.patch"

SRC_URI[md5sum] = "51c792ba7b874ad8c43f0d3da4cfabe0"
SRC_URI[sha256sum] = "634964ae18ef68c8493add2ce150e3b4502badeb0d9194b4bd81241d25e6735c"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

INITSCRIPT_NAME = "fbsetup"
INITSCRIPT_PARAMS = "start 0 S ."

do_configure () {
	./configure --with-x86emu
}

do_compile () {
	KDIR="${STAGING_DIR_HOST}/usr" make
}

do_install () {
	install -d ${D}${base_sbindir}
	install v86d ${D}${base_sbindir}/

        # Only install fbsetup script if 'sysvinit' is in DISTRO_FEATURES
        if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
            install -d ${D}${sysconfdir}/init.d/
            install -m 0755 ${WORKDIR}/fbsetup ${D}${sysconfdir}/init.d/fbsetup
        fi

        # Install systemd related configuration file
        if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
            install -d ${D}${sysconfdir}/modules-load.d
            install -m 0644 ${WORKDIR}/uvesafb.conf ${D}${sysconfdir}/modules-load.d
        fi
}

# As the recipe doesn't inherit systemd.bbclass, we need to set this variable
# manually to avoid unnecessary postinst/preinst generated.
python __anonymous() {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}

inherit update-rc.d

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"
pkg_postinst_${PN} () {
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd sysvinit','true','false',d)}; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		systemctl $OPTS mask fbsetup.service
	fi
}
