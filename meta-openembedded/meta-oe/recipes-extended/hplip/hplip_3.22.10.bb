SUMMARY = "HP Linux Imaging and Printing"
LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=20f2c819499cc2063e9a7b07b408815c"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz \
           file://configure.patch \
           file://fix-libusb-paths.patch \
           file://600-fix.patch \
           file://030-replace_unsafe_memcpy_with_memmove.patch \
           file://050-fix-glibcisms.patch \
           file://hplip-3.19.6-fix-return.patch \
           file://0001-common-utils-Include-string.h-for-strcasestr.patch \
           file://0002-Add-ImageProcessor-only-when-DISBALE_IMAGEPROCESSOR_.patch \
           file://0003-pserror.c-Define-column-to-be-int-explcitly.patch \
           file://0004-Define-missing-prototype-for-functions.patch \
           file://0005-hp_ipp.c-Add-printf-format-to-snprintf-calls.patch \
           file://0006-Workaround-patch-for-missing-Python3-transition-of-t.patch \
           file://0001-Fix-installing-ipp-usb-quirk.patch \
           file://0001-Drop-using-register-storage-classifier.patch"
SRC_URI[sha256sum] = "533c3f2f6b53e4163ded4fd81d1f11ae6162a0f6451bd5e62a8382d0c1366624"

UPSTREAM_CHECK_URI = "https://sourceforge.net/p/hplip/activity"

DEPENDS += "cups python3 libusb1 python3-setuptools-native"

inherit autotools-brokensep python3-dir python3native python3targetconfig pkgconfig systemd

export STAGING_INCDIR
export STAGING_LIBDIR

CFLAGS += "-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}${PYTHON_ABI}"

EXTRA_OECONF += "\
        --enable-cups-drv-install \
        --enable-cups-ppd-install \
        --disable-network-build \
        --disable-doc-build \
        --disable-pp-build \
        --disable-scan-build \
        --disable-gui-build \
        --disable-fax-build \
        --disable-policykit  \
        --disable-qt4 \
        --disable-qt3 \
        --disable-dbus-build \
        --enable-foomatic-drv-install \
        --disable-foomatic-ppd-install \
        --disable-foomatic-rip-hplip-install \
        --disable-imageProcessor_build \
        --with-cupsbackenddir=${libexecdir}/cups/backend \
        --with-cupsfilterdir=${libexecdir}/cups/filter \
"

EXTRA_OEMAKE = "CPPFLAGS='${CFLAGS}' rulessystemdir=${systemd_unitdir}/system/"

do_install:append() {
    rm -rf ${D}${datadir}/hplip/upgrade.py
    rm -rf ${D}${datadir}/hplip/uninstall.py
    sed -i -e "s|/usr/bin/env python|/usr/bin/env python3|g" ${D}${datadir}/hplip/*.py
    sed -i -e "s|/usr/bin/python|/usr/bin/env python3|g" ${D}${datadir}/hplip/*.py
}

PACKAGE_BEFORE_PN += "${PN}-ppd ${PN}-cups ${PN}-backend ${PN}-filter ${PN}-hal"

RDEPENDS:${PN} += " \
        python3\
        python3-syslog \
        python3-pprint \
        python3-compression \
        python3-shell \
        python3-xml \
        python3-unixadmin \
        python3-html \
        python3-resource \
        python3-terminal \
"
RDEPENDS:${PN}-filter += "perl ghostscript"

FILES:${PN} += "${datadir}/ipp-usb/quirks/HPLIP.conf"
FILES:${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/*.la"
FILES:${PN}-ppd = "${datadir}/ppd"
FILES:${PN}-cups = "${datadir}/cups"
FILES:${PN}-backend = "${libexecdir}/cups/backend"
FILES:${PN}-filter = "${libexecdir}/cups/filter"
FILES:${PN}-hal = "${datadir}/hal"

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/*.so"

SYSTEMD_SERVICE:${PN} = "hplip-printer@.service"

CLEANBROKEN = "1"
