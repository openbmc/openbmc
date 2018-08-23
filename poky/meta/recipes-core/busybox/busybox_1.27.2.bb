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
           file://runlevel \
           file://makefile-libbb-race.patch \
           file://CVE-2011-5325.patch \
           file://CVE-2017-15873.patch \
           file://busybox-CVE-2017-16544.patch \
           file://busybox-fix-lzma-segfaults.patch \
           file://umount-ignore-c.patch \
           file://CVE-2017-15874.patch \
"
SRC_URI_append_libc-musl = " file://musl.cfg "

SRC_URI[tarball.md5sum] = "476186f4bab81781dab2369bfd42734e"
SRC_URI[tarball.sha256sum] = "9d4be516b61e6480f156b11eb42577a13529f75d3383850bb75c50c285de63df"
