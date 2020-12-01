require busybox.inc

SRC_URI = "https://busybox.net/downloads/busybox-${PV}.tar.bz2;name=tarball \
           file://busybox-udhcpc-no_deconfig.patch \
           file://find-touchscreen.sh \
           file://busybox-cron \
           file://busybox-httpd \
           file://busybox-udhcpd \
           file://default.script \
           file://simple.script \
           file://hwclock.sh \
           file://syslog \
           file://syslog-startup.conf \
           file://syslog.conf \
           file://busybox-syslog.default \
           file://mdev \
           file://mdev.conf \
           file://mdev-mount.sh \
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
           file://unicode.cfg \
           file://rcS \
           file://rcK \
           file://makefile-libbb-race.patch \
           file://0001-testsuite-check-uudecode-before-using-it.patch \
           file://0001-testsuite-use-www.example.org-for-wget-test-cases.patch \
           file://0001-du-l-works-fix-to-use-145-instead-of-144.patch \
           file://0001-date-Use-64-prefix-syscall-if-we-have-to.patch \
           file://0001-time-Use-64-prefix-syscall-if-we-have-to.patch \
           file://0003-runsv-Use-64-prefix-syscall-if-we-have-to.patch \
           file://0001-Remove-syscall-wrappers-around-clock_gettime-closes-.patch \
           file://0001-Remove-stime-function-calls.patch \
           file://0001-sysctl-ignore-EIO-of-stable_secret-below-proc-sys-ne.patch \
           file://busybox-CVE-2018-1000500.patch \
           file://0001-hwclock-make-glibc-2.31-compatible.patch \
"
SRC_URI_append_libc-musl = " file://musl.cfg "

SRC_URI[tarball.md5sum] = "70913edaf2263a157393af07565c17f0"
SRC_URI[tarball.sha256sum] = "d0f940a72f648943c1f2211e0e3117387c31d765137d92bd8284a3fb9752a998"
