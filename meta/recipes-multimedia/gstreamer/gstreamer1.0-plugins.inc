SUMMARY = "Plugins for the GStreamer multimedia framework 1.x"
HOMEPAGE = "http://gstreamer.freedesktop.org/"
BUGTRACKER = "https://bugzilla.gnome.org/enter_bug.cgi?product=Gstreamer"
SECTION = "multimedia"
DEPENDS = "gstreamer1.0"

inherit autotools pkgconfig

GSTREAMER_1_0_DEBUG ?= "--disable-debug"
GSTREAMER_1_0_GIT_BRANCH ?= "master"
EXTRA_OECONF = "--disable-valgrind ${GSTREAMER_1_0_DEBUG} --disable-examples "

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

LIBV = "1.0"
require gst-plugins-package.inc

PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc orc-native"

export ORCC = "${STAGING_DIR_NATIVE}${bindir}/orcc"

PACKAGES_DYNAMIC = "^${PN}-.*"

# apply gstreamer hack after Makefile.in.in in source is replaced by our version from
# ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in, but before configure is executed 
# http://lists.linuxtogo.org/pipermail/openembedded-core/2012-November/032233.html
oe_runconf_prepend() {
	if [ -e ${S}/po/Makefile.in.in ]; then
		sed -i -e "1a\\" -e 'GETTEXT_PACKAGE = @GETTEXT_PACKAGE@' ${S}/po/Makefile.in.in
	fi
}

SRC_URI = "${@get_gst_srcuri(d)}"

def get_gst_srcuri(d):
    # check if expected prefix is present
    prefix = "gstreamer1.0-"
    bpn = d.getVar("BPN", True)
    if not bpn.startswith(prefix):
        bb.fatal('Invalid GStreamer 1.0 plugin package name "%s" : must start with "%s"' % (bpn, prefix))

    # replaced prefix with "gst-", which is what is used for the tarball and repository filenames
    gstpkg_basename = "gst-" + bpn[len(prefix):]
    pv = d.getVar("PV", True)
    branch = d.getVar("GSTREAMER_1_0_GIT_BRANCH", True)

    if pv == "git":
        s = "git://anongit.freedesktop.org/gstreamer/%s;branch=%s" % (gstpkg_basename, branch)
    else:
        s = "http://gstreamer.freedesktop.org/src/%s/%s-%s.tar.xz" % (gstpkg_basename, gstpkg_basename, pv)
    return s

delete_liblink_m4_file() {
	# This m4 file contains nastiness which conflicts with libtool 2.2.2
	rm "${S}/m4/lib-link.m4" || true
}

do_configure[prefuncs] += " delete_liblink_m4_file "

