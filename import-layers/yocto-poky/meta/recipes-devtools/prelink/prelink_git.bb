SECTION = "devel"
# Need binutils for libiberty.a
# Would need transfig-native for documentation if it wasn't disabled
DEPENDS = "elfutils binutils"
SUMMARY = "An ELF prelinking utility"
DESCRIPTION = "The prelink package contains a utility which modifies ELF shared libraries \
and executables, so that far fewer relocations need to be resolved at \
runtime and thus programs come up faster."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"
SRCREV = "927979bbd115eeb8a75db3231906ef6aca4c4eb6"
PV = "1.0+git${SRCPV}"

#
# The cron script attempts to re-prelink the system daily -- on
# systems where users are adding applications, this might be reasonable
# but for embedded, we should be re-running prelink -a after an update.
#
# Default is prelinking is enabled.
#
SUMMARY_${PN}-cron = "Cron scripts to control automatic prelinking"
DESCRIPTION_${PN}-cron = "Cron scripts to control automatic prelinking.  \
See: ${sysconfdir}/cron.daily/prelink for configuration information."

FILES_${PN}-cron = "${sysconfdir}/cron.daily ${sysconfdir}/default"

PACKAGES =+ "${PN}-cron"

SRC_URI = "git://git.yoctoproject.org/prelink-cross.git;branch=cross_prelink \
           file://prelink.conf \
           file://prelink.cron.daily \
           file://prelink.default \
	   file://macros.prelink"

TARGET_OS_ORIG := "${TARGET_OS}"
OVERRIDES_append = ":${TARGET_OS_ORIG}"

S = "${WORKDIR}/git"

inherit autotools 

BBCLASSEXTEND = "native"

EXTRA_OECONF = "--disable-selinux --with-pkgversion=${PV}-${PR} \
	--with-bugurl=http://bugzilla.yoctoproject.org/"

do_configure_prepend () {
        # Disable documentation!
        echo "all:" > ${S}/doc/Makefile.am
}

do_install_append () {
	install -d ${D}${sysconfdir}/cron.daily ${D}${sysconfdir}/default ${D}${sysconfdir}/rpm
	install -m 0644 ${WORKDIR}/prelink.conf ${D}${sysconfdir}/prelink.conf
	install -m 0644 ${WORKDIR}/prelink.cron.daily ${D}${sysconfdir}/cron.daily/prelink
	install -m 0644 ${WORKDIR}/prelink.default ${D}${sysconfdir}/default/prelink
	install -m 0644 ${WORKDIR}/macros.prelink ${D}${sysconfdir}/rpm/macros.prelink
}

# If we're using image-prelink, we want to skip this on the host side
# but still do it if the package is installed on the target...
pkg_postinst_prelink() {
#!/bin/sh

if [ "x$D" != "x" ]; then
  ${@bb.utils.contains('USER_CLASSES', 'image-prelink', 'exit 0', 'exit 1', d)}
fi

prelink -a
}

pkg_prerm_prelink() {
#!/bin/sh

if [ "x$D" != "x" ]; then
  exit 1
fi

prelink -au
}

