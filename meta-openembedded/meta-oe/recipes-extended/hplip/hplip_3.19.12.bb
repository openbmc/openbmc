SUMMARY = "HP Linux Imaging and Printing"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=20f2c819499cc2063e9a7b07b408815c"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz \
           file://configure.patch \
           file://fix-libusb-paths.patch \
           file://999-remove-lImageProcessor.patch \
           file://600-fix.patch \
           file://030-replace_unsafe_memcpy_with_memmove.patch \
           file://050-fix-glibcisms.patch \
           file://hplip-3.19.6-fix-return.patch \
"
SRC_URI[md5sum] = "d72bc77d791c150c2c22b84e9553bab3"
SRC_URI[sha256sum] = "b7f398502fb659e0de8e54976237e3c6a64fec0b3c36054a515876f7b006b255"

DEPENDS += "cups python3 libusb"

inherit autotools-brokensep python3-dir python3native pkgconfig systemd

export STAGING_INCDIR
export STAGING_LIBDIR

CFLAGS += "-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}${PYTHON_ABI}"

EXTRA_OECONF += "\
        LIBUSBINCLUDEROOT=${STAGING_INCDIR} \
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
        --with-cupsbackenddir=${libdir}/cups/backend \
        --with-cupsfilterdir=${libdir}/cups/filter \
"

EXTRA_OEMAKE = "rulessystemdir=${systemd_unitdir}/system/"

do_install_append() {
    rm -rf ${D}${datadir}/hplip/upgrade.py
    rm -rf ${D}${datadir}/hplip/uninstall.py
    sed -i -e "s|/usr/bin/env python|/usr/bin/env python3|g" ${D}${datadir}/hplip/*.py
    sed -i -e "s|/usr/bin/python|/usr/bin/env python3|g" ${D}${datadir}/hplip/*.py
}

PACKAGES += "${PN}-ppd ${PN}-cups ${PN}-backend ${PN}-filter ${PN}-hal"

RDEPENDS_${PN} += " \
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

SYSTEMD_SERVICE_${PN} = "hplip-printer@.service"

CLEANBROKEN = "1"
