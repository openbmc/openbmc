SUMMARY = "netcf"
DESCRIPTION = "netcf is a cross-platform network configuration library."
HOMEPAGE = "https://pagure.io/netcf"
SECTION = "libs"
LICENSE = "LGPLv2.1"

LIC_FILES_CHKSUM = "file://COPYING;md5=fb919cc88dbe06ec0b0bd50e001ccf1f"

SRCREV = "2c5d4255857531bc09d91dcd02e86545f29004d4"
PV .= "+git${SRCPV}"

SRC_URI = "git://pagure.io/netcf.git;protocol=https \
"

UPSTREAM_CHECK_GITTAGREGEX = "release-(?P<pver>(\d+(\.\d+)+))"

DEPENDS += "augeas libnl libxslt libxml2"

do_configure[depends] += "${MLPREFIX}gnulib:do_populate_sysroot"

S = "${WORKDIR}/git"

inherit gettext autotools perlnative pkgconfig systemd

EXTRA_OECONF_append_class-target = " --with-driver=redhat"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-sysinit=systemd,--with-sysinit=initscripts,"

EXTRA_AUTORECONF += "-I ${S}/gnulib/m4"

do_configure_prepend() {
    currdir=`pwd`
    cd ${S}

    # avoid bootstrap cloning gnulib on every configure
    # the dir starts out empty from the pkg, but unconditionally blow it
    # away so if we reconfigure due to gnulib sysroot sig changes, we will
    # get the newer gnulib content into the build here.
    rm -rf ${S}/.gnulib
    cp -rf ${STAGING_DATADIR}/gnulib ${S}/.gnulib

    # --force to avoid errors on reconfigure e.g if recipes changed we depend on
    # | bootstrap: running: libtoolize --quiet
    # | libtoolize:   error: 'libltdl/COPYING.LIB' exists: use '--force' to overwrite
    # | ...
    ./bootstrap --force --no-git --gnulib-srcdir=.gnulib

    cd $currdir
}

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
       install -d ${D}${systemd_unitdir}/system
       if [ -d "${D}${libdir}/systemd/system" ]; then
           if [ "${systemd_unitdir}" != "${libdir}/systemd" ] ; then 
               mv ${D}${libdir}/systemd/system/* ${D}${systemd_unitdir}/system/
               rm -rf ${D}${libdir}/systemd/
	   fi
       elif [ "${systemd_unitdir}" != "${nonarch_libdir}/systemd" ] ; then 
           mv ${D}${nonarch_libdir}/systemd/system/* ${D}${systemd_unitdir}/system/
           rm -rf ${D}${nonarch_libdir}/systemd/
       fi
    else
       mv ${D}${sysconfdir}/rc.d/init.d/ ${D}${sysconfdir}
       rm -rf ${D}${sysconfdir}/rc.d/
    fi
}

FILES_${PN} += " \
        ${libdir} \
        ${nonarch_libdir} \
        "

SYSTEMD_SERVICE_${PN} = "netcf-transaction.service"
