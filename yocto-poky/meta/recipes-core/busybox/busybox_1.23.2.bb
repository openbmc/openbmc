require busybox.inc

SRC_URI = "http://www.busybox.net/downloads/busybox-${PV}.tar.bz2;name=tarball \
           file://get_header_tar.patch \
           file://busybox-appletlib-dependency.patch \
           file://busybox-udhcpc-no_deconfig.patch \
           file://find-touchscreen.sh \
           file://busybox-cron \
           file://busybox-httpd \
           file://busybox-udhcpd \
           file://default.script \
           file://simple.script \
           file://hwclock.sh \
           file://mount.busybox \
           file://syslog \
           file://syslog-startup.conf \
           file://syslog.conf \
           file://busybox-syslog.default \
           file://mdev \
           file://mdev.conf \
           file://mdev-mount.sh \
           file://umount.busybox \
           file://defconfig \
           file://busybox-syslog.service.in \
           file://busybox-klogd.service.in \
           file://fail_on_no_media.patch \
           file://run-ptest \
           file://inetd.conf \
           file://inetd \
           file://login-utilities.cfg \
           file://recognize_connmand.patch \
           file://busybox-cross-menuconfig.patch \
           file://0001-Switch-to-POSIX-utmpx-API.patch \
           file://0001-ifconfig-fix-double-free-fatal-error-in-INET_sprint.patch \
           file://0001-chown-fix-help-text.patch \
           file://0001-Use-CC-when-linking-instead-of-LD-and-use-CFLAGS-and.patch \
           file://0002-Passthrough-r-to-linker.patch \
           file://0001-randconfig-fix.patch \
           file://mount-via-label.cfg \
           file://sha1sum.cfg \
           file://sha256sum.cfg \
           file://getopts.cfg \
"

SRC_URI[tarball.md5sum] = "7925683d7dd105aabe9b6b618d48cc73"
SRC_URI[tarball.sha256sum] = "05a6f9e21aad8c098e388ae77de7b2361941afa7157ef74216703395b14e319a"

EXTRA_OEMAKE += "V=1 ARCH=${TARGET_ARCH} CROSS_COMPILE=${TARGET_PREFIX} SKIP_STRIP=y"

do_install_ptest () {
        cp -r ${B}/testsuite ${D}${PTEST_PATH}/
        cp ${B}/.config      ${D}${PTEST_PATH}/
        ln -s /bin/busybox   ${D}${PTEST_PATH}/busybox
}
