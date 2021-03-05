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
           ${@["", "file://rcS.default"][(d.getVar('VIRTUAL-RUNTIME_init_manager') == 'busybox')]} \
           ${@["", "file://mdev.cfg"][(d.getVar('VIRTUAL-RUNTIME_dev_manager') == 'busybox-mdev')]} \
           file://syslog.cfg \
           file://unicode.cfg \
           file://rcS \
           file://rcK \
           file://makefile-libbb-race.patch \
           file://0001-testsuite-check-uudecode-before-using-it.patch \
           file://0001-testsuite-use-www.example.org-for-wget-test-cases.patch \
           file://0001-du-l-works-fix-to-use-145-instead-of-144.patch \
           file://0001-sysctl-ignore-EIO-of-stable_secret-below-proc-sys-ne.patch \
           file://rev.cfg \
           file://pgrep.cfg \
"
SRC_URI_append_libc-musl = " file://musl.cfg "

SRC_URI[tarball.sha256sum] = "d568681c91a85edc6710770cebc1e80e042ad74d305b5c2e6d57a5f3de3b8fbd"
