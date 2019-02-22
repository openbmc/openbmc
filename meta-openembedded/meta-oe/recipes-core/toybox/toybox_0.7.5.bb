SUMMARY = "Toybox combines common utilities together into a single executable."
HOMEPAGE = "http://www.landley.net/toybox/"
DEPENDS = "attr virtual/crypt"

LICENSE = "BSD-0-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0b8b3dd6431bcaa245da0a08bd0d511"

SRC_URI = "http://www.landley.net/toybox/downloads/${BPN}-${PV}.tar.gz \
           file://OE-path-changes.patch \
           "
SRC_URI[md5sum] = "a8bb502a1be941f06dd2644fff25f547"
SRC_URI[sha256sum] = "3ada450ac1eab1dfc352fee915ea6129b9a4349c1885f1394b61bd2d89a46c04"

SECTION = "base"

TOYBOX_BIN = "generated/unstripped/toybox"

EXTRA_OEMAKE = 'HOSTCC="${BUILD_CC}" CPUS=${@oe.utils.cpu_count()}'

do_configure() {
    oe_runmake defconfig

    # Disable killall5 as it isn't managed by update-alternatives
    sed -e 's/CONFIG_KILLALL5=y/# CONFIG_KILLALL5 is not set/' -i .config

    # Disable swapon as it doesn't handle the '-a' argument used during boot
    sed -e 's/CONFIG_SWAPON=y/# CONFIG_SWAPON is not set/' -i .config
}

do_compile() {
    oe_runmake ${TOYBOX_BIN}

    # Create a list of links needed
    ${BUILD_CC} -I . scripts/install.c -o generated/instlist
    ./generated/instlist long | sed -e 's#^#/#' > toybox.links
}

do_install() {
    # Install manually instead of using 'make install'
    install -d ${D}${base_bindir}
    if grep -q "CONFIG_TOYBOX_SUID=y" ${B}/.config; then
        install -m 4755 ${B}/${TOYBOX_BIN} ${D}${base_bindir}/toybox
    else
        install -m 0755 ${B}/${TOYBOX_BIN} ${D}${base_bindir}/toybox
    fi

    install -d ${D}${sysconfdir}
    install -m 0644 ${B}/toybox.links ${D}${sysconfdir}
}

inherit update-alternatives

# If you've chosen to install toybox you probably want it to take precedence
# over busybox where possible but not over other packages
ALTERNATIVE_PRIORITY = "60"

python do_package_prepend () {
    # Read links from /etc/toybox.links and create appropriate
    # update-alternatives variables

    dvar = d.getVar('D')
    pn = d.getVar('PN')
    target = d.expand("${base_bindir}/toybox")

    f = open('%s/etc/toybox.links' % (dvar), 'r')
    for alt_link_name in f:
        alt_link_name = alt_link_name.strip()
        alt_name = os.path.basename(alt_link_name)
        d.appendVar('ALTERNATIVE_%s' % (pn), ' ' + alt_name)
        d.setVarFlag('ALTERNATIVE_LINK_NAME', alt_name, alt_link_name)
        d.setVarFlag('ALTERNATIVE_TARGET', alt_name, target)
    f.close()
}
