SUMMARY = "Toybox combines common utilities together into a single executable."
HOMEPAGE = "http://www.landley.net/toybox/"
DEPENDS = "attr virtual/crypt"

LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78659a599b9325da368f2f1eb88f19c7"

inherit cml1 update-alternatives

SRC_URI = "http://www.landley.net/toybox/downloads/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "15aa3f832f4ec1874db761b9950617f99e1e38144c22da39a71311093bfe67dc"

SECTION = "base"

RDEPENDS:${PN} = "${@["", "toybox-inittab"][(d.getVar('VIRTUAL-RUNTIME_init_manager') == 'toybox')]}"

TOYBOX_BIN = "generated/unstripped/toybox"

# Toybox is strict on what CC, CFLAGS and CROSS_COMPILE variables should contain.
# Fix CC, CFLAGS, CROSS_COMPILE to match expectations.
# CC = compiler name
# CFLAGS = only compiler flags
# CROSS_COMPILE = compiler prefix
CFLAGS += "${TOOLCHAIN_OPTIONS} ${TUNE_CCARGS}"

COMPILER:toolchain-clang = "clang"
COMPILER  ?= "gcc"

PACKAGECONFIG ??= "no-iconv no-getconf"

PACKAGECONFIG[no-iconv] = ",,"
PACKAGECONFIG[no-getconf] = ",,"

EXTRA_OEMAKE = 'CROSS_COMPILE="${HOST_PREFIX}" \
                CC="${COMPILER}" \
                STRIP="strip" \
                CFLAGS="${CFLAGS}" \
                HOSTCC="${BUILD_CC}" CPUS=${@oe.utils.cpu_count()} V=1'

do_configure() {
    # allow user to define their own defconfig in bbappend, taken from kernel.bbclass
    if [ "${S}" != "${B}" ] && [ -f "${S}/.config" ] && [ ! -f "${B}/.config" ]; then
        mv "${S}/.config" "${B}/.config"
    fi

    # Copy defconfig to .config if .config does not exist. This allows
    # recipes to manage the .config themselves in do_configure:prepend().
    if [ -f "${WORKDIR}/defconfig" ] && [ ! -f "${B}/.config" ]; then
        cp "${WORKDIR}/defconfig" "${B}/.config"
    fi

    oe_runmake oldconfig || oe_runmake defconfig

    # Disable killall5 as it isn't managed by update-alternatives
    sed -e 's/CONFIG_KILLALL5=y/# CONFIG_KILLALL5 is not set/' -i .config

    # Disable swapon as it doesn't handle the '-a' argument used during boot
    sed -e 's/CONFIG_SWAPON=y/# CONFIG_SWAPON is not set/' -i .config

    # Enable init if toybox was set as init manager
    if ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','toybox','true','false',d)}; then
        sed -e 's/# CONFIG_INIT is not set/CONFIG_INIT=y/' -i .config
    fi
}

do_compile() {
    oe_runmake ${TOYBOX_BIN}

    # Create a list of links needed
    ${BUILD_CC} -I . scripts/install.c -o generated/instlist
    ./generated/instlist long | sed -e 's#^#/#' > toybox.links
    if ${@bb.utils.contains('PACKAGECONFIG','no-iconv','true','false',d)}; then
        sed -i -e '/iconv$/d' toybox.links
    fi
    if ${@bb.utils.contains('PACKAGECONFIG','no-getconf','true','false',d)}; then
        sed -i -e '/getconf$/d' toybox.links
    fi
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

# If you've chosen to install toybox you probably want it to take precedence
# over busybox where possible but not over other packages
ALTERNATIVE_PRIORITY = "60"

python do_package:prepend () {
    # Read links from /etc/toybox.links and create appropriate
    # update-alternatives variables

    dvar = d.getVar('D')
    pn = d.getVar('PN')
    target = d.expand("${base_bindir}/toybox")

    f = open('%s/etc/toybox.links' % (dvar), 'r')
    for alt_link_name in f:
        alt_link_name = alt_link_name.strip()
        alt_name = os.path.basename(alt_link_name)
        d.appendVar('ALTERNATIVE:%s' % (pn), ' ' + alt_name)
        d.setVarFlag('ALTERNATIVE_LINK_NAME', alt_name, alt_link_name)
        d.setVarFlag('ALTERNATIVE_TARGET', alt_name, target)
    f.close()
}
