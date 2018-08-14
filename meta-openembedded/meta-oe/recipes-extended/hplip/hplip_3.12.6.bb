SUMMARY = "HP Linux Imaging and Printing"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=442bb3cbbeeb60643a87325718b8a8ee"

PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz \
           file://setup-add-sleep-after-cups-reset.patch \
           file://fix-libusb-paths.patch \
           file://cups-1.6.patch \
           file://configure.patch \
           file://0001-include-cups-ppd.h-for-missing-ppd-definitions.patch \
"

DEPENDS += "cups python libusb"

inherit autotools-brokensep python-dir pythonnative pkgconfig

export STAGING_INCDIR
export STAGING_LIBDIR

EXTRA_OECONF += "\
        LIBUSBINCLUDEROOT=${STAGING_INCDIR} \
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
        --disable-foomatic-drv-install \
        --enable-foomatic-ppd-install  \
        --enable-foomatic-rip-hplip-install \
        --with-cupsbackenddir=${libdir}/cups/backend \
        --with-cupsfilterdir=${libdir}/cups/filter \
"

PACKAGES += "${PN}-ppd ${PN}-cups ${PN}-backend ${PN}-filter ${PN}-hal"

RDEPENDS_${PN} += " \
        python-syslog \
        python-pprint \
        python-compression \
        python-shell \
        python-xml \
        python-unixadmin \
        python-html \
        python-resource \
        python-terminal \
        python-subprocess\
"
RDEPENDS_${PN}-filter += "perl"

# need to snag the debug file or OE will fail on backend package
FILES_${PN}-dbg += "\
        ${libdir}/cups/backend/.debug \
        ${PYTHON_SITEPACKAGES_DIR}/.debug \
        ${libdir}/cups/filter/.debug "

FILES_${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/*.la"
FILES_${PN}-ppd = "${datadir}/ppd"
FILES_${PN}-cups = "${datadir}/cups"
FILES_${PN}-backend = "${libdir}/cups/backend"
FILES_${PN}-filter = "${libdir}/cups/filter"
FILES_${PN}-hal = "${datadir}/hal"

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}/*.so"

SRC_URI[md5sum] = "5303938e8630775ea6fb383af85775e5"
SRC_URI[sha256sum] = "54578000792969adb583e75efeacb9c46ab69659ec7e9424de390613f3595775"

