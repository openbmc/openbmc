SUMMARY = "Initramfs generator using udev"
HOMEPAGE = "https://dracut.wiki.kernel.org/index.php/Main_Page"
DESCRIPTION = "Dracut is an event driven initramfs infrastructure. dracut (the tool) is used to create an initramfs image by copying tools and files from an installed system and combining it with the dracut framework, usually found in /usr/lib/dracut/modules.d."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PE = "1"

SRCREV = "956c08774074ddc45b2f975e13d5c13d1fc36eff"
SRC_URI = "git://github.com/dracut-ng/dracut-ng.git;protocol=http;branch=main \
           file://0001-feat-dracut-install-split-ldd-command-arguments-for-.patch \
           "

DEPENDS += "kmod"
DEPENDS:append:libc-musl = " fts"

inherit bash-completion pkgconfig

S = "${WORKDIR}/git"

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

EXTRA_OEMAKE += 'libdir=${nonarch_libdir} LDLIBS="${LDLIBS}" enable_test=no'

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

FILES:${PN} += "${nonarch_libdir}/kernel \
                ${nonarch_libdir}/dracut \
                ${systemd_unitdir} \
               "
FILES:${PN}-dbg += "${nonarch_libdir}/dracut/.debug"

CONFFILES:${PN} += "${sysconfdir}/dracut.conf"

RDEPENDS:${PN} = "findutils cpio util-linux-blkid util-linux-getopt util-linux bash ldd"

# This could be optimized a bit, but let's avoid non-booting systems :)
RRECOMMENDS:${PN} = "kernel-modules \
                     coreutils \
                    "

CVE_STATUS[CVE-2010-4176] = "not-applicable-platform: Applies only to Fedora"
