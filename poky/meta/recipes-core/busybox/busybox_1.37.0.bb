require busybox.inc

SRC_URI = "https://busybox.net/downloads/busybox-${PV}.tar.bz2;name=tarball \
           file://0001-depmod-Ignore-.debug-directories.patch \
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
           file://busybox-1.36.1-no-cbq.patch \
           file://0001-cut-Fix-s-flag-to-omit-blank-lines.patch \
           file://0001-syslogd-fix-wrong-OPT_locallog-flag-detection.patch \
           file://0002-start-stop-daemon-fix-tests.patch \
           file://0003-start-stop-false.patch \
           "
SRC_URI:append:libc-musl = " file://musl.cfg"
SRC_URI:append:x86-64 = " file://sha_accel.cfg"
SRC_URI[tarball.sha256sum] = "3311dff32e746499f4df0d5df04d7eb396382d7e108bb9250e7b519b837043a4"
