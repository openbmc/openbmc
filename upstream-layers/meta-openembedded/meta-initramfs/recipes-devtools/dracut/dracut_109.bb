SUMMARY = "Initramfs generator using udev"
HOMEPAGE = "https://dracut.wiki.kernel.org/index.php/Main_Page"
DESCRIPTION = "Dracut is an event driven initramfs infrastructure. dracut (the tool) is used to create an initramfs image by copying tools and files from an installed system and combining it with the dracut framework, usually found in /usr/lib/dracut/modules.d."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PE = "1"

SRCREV = "13c5e5feee9ba86b960d351f87aa0eb25f242ff0"
SRC_URI = "git://github.com/dracut-ng/dracut-ng.git;protocol=http;branch=main;tag=${PV} \
           file://0002-fix-broken-symlink-in-dracut-config-examples.patch \
           "

DEPENDS += "kmod"
DEPENDS:append:libc-musl = " fts"

inherit bash-completion pkgconfig

EXTRA_OECONF = "--prefix=${prefix} \
                --libdir=${nonarch_libdir} \
                --datadir=${datadir} \
                --sysconfdir=${sysconfdir} \
                --sbindir=${sbindir} \
                --bindir=${bindir} \
                --includedir=${includedir} \
                --localstatedir=${localstatedir} \
                --disable-documentation \
               "

# RDEPEND on systemd optionally
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,,,systemd"

EXTRA_OEMAKE += 'libdir=${nonarch_libdir} LDLIBS="${LDLIBS}" enable_test=no DRACUT_FULL_VERSION=${PV}'

CFLAGS:append = " -fPIC"
LDLIBS:append:libc-musl = " -lfts"

do_configure() {
    ./configure ${EXTRA_OECONF}
}

do_install() {
    oe_runmake install DESTDIR=${D}
    # Its Makefile uses cp -arx to install modules.d, so fix the owner
    # to root:root
    chown -R root:root ${D}/${nonarch_libdir}/dracut/modules.d \
        ${D}/${nonarch_libdir}/dracut/dracut.conf.d

    if ! ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        rm -rf ${D}${nonarch_libdir}/systemd
    fi
}

do_install:append:class-target () {
    # Generate and install a config file listing where the DISTRO puts things, dracut
    # is not always savvy enough to figure it out by itself
    # Since this primarily fixes systemd issues, only install it when using systemd.
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        cat << EOF > ${B}/${DISTRO}.conf
stdloglvl=3
sysloglvl=5
sysctlconfdir=${sysconfdir}/sysctl.d
systemdutildir=${systemd_unitdir}
systemdutilconfdir=${sysconfdir}/systemd
systemdcatalog=${systemd_unitdir}catalog
systemdntpunits=${systemd_unitdir}ntp-units.d
systemdntpunitsconfdir=${sysconfdir}/systemd/ntp-units.d
systemdportable=${systemd_unitdir}/portable
systemdportableconfdir=${sysconfdir}/systemd/portable
systemdsystemunitdir=${systemd_system_unitdir}
systemdsystemconfdir=${sysconfdir}/systemd/system
systemduser=${systemd_user_unitdir}
systemduserconfdir=${sysconfdir}/systemd/user
EOF
        install -m 0644 ${B}/${DISTRO}.conf ${D}${nonarch_libdir}/dracut/dracut.conf.d/
    fi
}


FILES:${PN} += "${nonarch_libdir}/kernel \
                ${nonarch_libdir}/dracut \
                ${systemd_unitdir} \
               "
FILES:${PN}-dbg += "${nonarch_libdir}/dracut/.debug"

CONFFILES:${PN} += "${sysconfdir}/dracut.conf"

# The native variant uses a non-ldd based method of getting library
# dependencies, so ldd is only needed on the target
RDEPENDS:${PN} = "findutils cpio util-linux-blkid util-linux-getopt util-linux bash"
RDEPENDS:${PN}:append:class-target = " ldd"

# This could be optimized a bit, but let's avoid non-booting systems :)
RRECOMMENDS:${PN}:class-target = "kernel-modules \
                                  coreutils \
                                 "

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2010-4176] = "not-applicable-platform: Applies only to Fedora"
