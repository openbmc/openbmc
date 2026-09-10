DESCRIPTION = "This daemon is in charge of multiplexing connections over USB to an iPhone or iPod touch."
HOMEPAGE = "https://github.com/libimobiledevice/usbmuxd"
LICENSE = "GPL-2.0-only AND GPL-3.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=ebb5c50ab7cab4baeffba14977030c07 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "udev libusb1 libplist libimobiledevice-glue"

inherit autotools pkgconfig gitpkgv systemd useradd

PKGV = "${GITPKGVTAG}"
PV = "1.1.2+git"

SRCREV = "01c94c77f59404924f1c46d99c4e5e0c7817281b"
SRC_URI = "git://github.com/libimobiledevice/usbmuxd;protocol=https;branch=master"

# The recipe deliberately tracks master ahead of the last release (1.1.1), so
# tag-based checks always report a regression; track new commits instead.
UPSTREAM_CHECK_COMMITS = "1"


EXTRA_OECONF += "--without-preflight"

FILES:${PN} += "${base_libdir}/udev/rules.d/"
FILES:${PN} += "${nonarch_libdir}/sysusers.d"

SYSTEMD_SERVICE:${PN} = "usbmuxd.service"

# 39-usbmuxd.rules carries OWNER="usbmux"; from systemd v258 udev drops the
# whole rule line when the user does not exist, so the daemon is never
# started. Create the account, and ship a sysusers.d fragment so it is also
# recreated on systems whose /etc predates this recipe.
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /bin/false --user-group usbmux"

do_install:append() {
    install -d ${D}${nonarch_libdir}/sysusers.d
    printf 'u usbmux - "usbmux daemon"\n' > ${D}${nonarch_libdir}/sysusers.d/usbmuxd.conf
}
