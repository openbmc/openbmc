require busybox.inc

SRC_URI = "https://busybox.net/downloads/busybox-${PV}.tar.bz2;name=tarball \
           file://0001-depmod-Ignore-.debug-directories.patch \
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
           file://mount-via-label.cfg \
           file://sha1sum.cfg \
           file://sha256sum.cfg \
           file://getopts.cfg \
           file://longopts.cfg \
           file://resize.cfg \
           ${@["", "file://init.cfg"][(d.getVar('VIRTUAL-RUNTIME_init_manager') == 'busybox')]} \
           ${@["", "file://rcS.default"][(d.getVar('VIRTUAL-RUNTIME_init_manager') == 'busybox')]} \
           ${@["", "file://mdev.cfg"][(d.getVar('VIRTUAL-RUNTIME_dev_manager') == 'busybox-mdev')]} \
           file://syslog.cfg \
           file://unicode.cfg \
           file://rev.cfg \
           file://pgrep.cfg \
           file://rcS \
           file://rcK \
           file://makefile-libbb-race.patch \
           file://0001-testsuite-check-uudecode-before-using-it.patch \
           file://0001-testsuite-use-www.example.org-for-wget-test-cases.patch \
           file://0001-du-l-works-fix-to-use-145-instead-of-144.patch \
           file://0001-sysctl-ignore-EIO-of-stable_secret-below-proc-sys-ne.patch \
           file://0001-libbb-sockaddr2str-ensure-only-printable-characters-.patch \
           file://0002-nslookup-sanitize-all-printed-strings-with-printable.patch \
           file://start-stop-false.patch \
           "
SRC_URI:append:libc-musl = " file://musl.cfg "
# TODO http://lists.busybox.net/pipermail/busybox/2023-January/090078.html
SRC_URI:append:x86 = " file://sha_accel.cfg"
SRC_URI[tarball.sha256sum] = "b8cc24c9574d809e7279c3be349795c5d5ceb6fdf19ca709f80cde50e47de314"
