require busybox.inc

SRC_URI = "http://www.busybox.net/downloads/busybox-${PV}.tar.bz2;name=tarball \
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
           file://0001-Use-CC-when-linking-instead-of-LD-and-use-CFLAGS-and.patch \
           file://mount-via-label.cfg \
           file://sha1sum.cfg \
           file://sha256sum.cfg \
           file://getopts.cfg \
           file://resize.cfg \
           ${@["", "file://init.cfg"][(d.getVar('VIRTUAL-RUNTIME_init_manager') == 'busybox')]} \
           ${@["", "file://mdev.cfg"][(d.getVar('VIRTUAL-RUNTIME_dev_manager') == 'busybox-mdev')]} \
           file://syslog.cfg \
           file://inittab \
           file://rcS \
           file://rcK \
           file://makefile-libbb-race.patch \
"
SRC_URI_append_libc-musl = " file://musl.cfg "

SRC_URI[tarball.md5sum] = "0a367e19cdfd157e8258d87f893ee516"
SRC_URI[tarball.sha256sum] = "97648636e579462296478e0218e65e4bc1e9cd69089a3b1aeb810bff7621efb7"
