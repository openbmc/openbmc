require busybox.inc

SRCREV = "be947c4d97c0dacb703a6f24dd813ff6dd3a33b6"
# Lookout for PV bump too when SRCREV is changed
PV = "1.23.2+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI = "git://busybox.net/busybox.git \
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
           file://0001-ifconfig-fix-double-free-fatal-error-in-INET_sprint.patch \
           file://0001-chown-fix-help-text.patch \
           file://mount-via-label.cfg \
           file://sha1sum.cfg \
           file://sha256sum.cfg \
"

EXTRA_OEMAKE += "V=1 ARCH=${TARGET_ARCH} CROSS_COMPILE=${TARGET_PREFIX} SKIP_STRIP=y"

do_install_ptest () {
        cp -r ${B}/testsuite ${D}${PTEST_PATH}/
        cp ${B}/.config      ${D}${PTEST_PATH}/
        ln -s /bin/busybox   ${D}${PTEST_PATH}/busybox
}

DEFAULT_PREFERENCE = "-1"
