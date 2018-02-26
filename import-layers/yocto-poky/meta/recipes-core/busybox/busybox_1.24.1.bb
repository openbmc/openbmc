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
           file://busybox-1.24.1-unzip.patch \
           file://busybox-1.24.1-unzip-regression.patch \
           file://busybox-1.24.1-truncate-open-mode.patch \
           file://0001-flock-update-the-behaviour-of-c-parameter-to-match-u.patch \
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
           file://CVE-2016-2148.patch \
           file://CVE-2016-2147.patch \
           file://CVE-2016-2147_2.patch \
           file://CVE-2016-6301.patch \
           file://ip_fix_problem_on_mips64_n64_big_endian_musl_systems.patch \
           file://makefile-fix-backport.patch \
           file://0001-sed-fix-sed-n-flushes-pattern-space-terminates-early.patch \
           file://busybox-kbuild-race-fix-commit-d8e61bb.patch \
           file://commit-applet_tables-fix-commit-0dddbc1.patch \
           file://makefile-libbb-race.patch \
           file://0001-libiproute-handle-table-ids-larger-than-255.patch \
           file://ifupdown-pass-interface-device-name-for-ipv6-route-c.patch \
           file://BUG9071_buffer_overflow_arp.patch \
           file://busybox-tar-add-IF_FEATURE_-checks.patch \
           file://0001-iproute-support-scope-.-Closes-8561.patch \
           file://0001-ip-fix-an-improper-optimization-req.r.rtm_scope-may-.patch \
"
SRC_URI_append_libc-musl = " file://musl.cfg "

SRC_URI[tarball.md5sum] = "be98a40cadf84ce2d6b05fa41a275c6a"
SRC_URI[tarball.sha256sum] = "37d03132cc078937360b392170b7a1d0e5b322eee9f57c0b82292a8b1f0afe3d"
