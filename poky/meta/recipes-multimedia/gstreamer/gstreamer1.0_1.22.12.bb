SUMMARY = "GStreamer 1.0 multimedia framework"
DESCRIPTION = "GStreamer is a multimedia framework for encoding and decoding video and sound. \
It supports a wide range of formats including mp3, ogg, avi, mpeg and quicktime."
HOMEPAGE = "http://gstreamer.freedesktop.org/"
BUGTRACKER = "https://bugzilla.gnome.org/enter_bug.cgi?product=Gstreamer"
SECTION = "multimedia"
LICENSE = "LGPL-2.1-or-later"

DEPENDS = "glib-2.0 glib-2.0-native libxml2 bison-native flex-native"

inherit meson pkgconfig gettext upstream-version-is-even gobject-introspection ptest-gnome

LIC_FILES_CHKSUM = "file://COPYING;md5=69333daa044cb77e486cc36129f7a770 \
                    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d"

S = "${WORKDIR}/gstreamer-${PV}"

SRC_URI = "https://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.xz \
           file://run-ptest \
           file://0001-tests-respect-the-idententaion-used-in-meson.patch \
           file://0002-tests-add-support-for-install-the-tests.patch \
           file://0003-tests-use-a-dictionaries-for-environment.patch \
           file://0004-tests-add-helper-script-to-run-the-installed_tests.patch \
           file://0005-allocator-Avoid-integer-overflow-when-allocating-sys.patch \
           "
SRC_URI[sha256sum] = "ac352f3d02caa67f3b169daa9aa78b04dea0fc08a727de73cb28d89bd54c6f61"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
                   check \
                   debug \
                   tools"

PACKAGECONFIG[debug] = "-Dgst_debug=true,-Dgst_debug=false"
PACKAGECONFIG[tracer-hooks] = "-Dtracer_hooks=true,-Dtracer_hooks=false"
PACKAGECONFIG[coretracers] = "-Dcoretracers=enabled,-Dcoretracers=disabled"
PACKAGECONFIG[check] = "-Dcheck=enabled,-Dcheck=disabled"
PACKAGECONFIG[tests] = "-Dtests=enabled -Dinstalled_tests=true,-Dtests=disabled -Dinstalled_tests=false"
PACKAGECONFIG[unwind] = "-Dlibunwind=enabled,-Dlibunwind=disabled,libunwind"
PACKAGECONFIG[dw] = "-Dlibdw=enabled,-Dlibdw=disabled,elfutils"
PACKAGECONFIG[bash-completion] = "-Dbash-completion=enabled,-Dbash-completion=disabled,bash-completion"
PACKAGECONFIG[tools] = "-Dtools=enabled,-Dtools=disabled"
PACKAGECONFIG[setcap] = "-Dptp-helper-permissions=capabilities,,libcap libcap-native"

# TODO: put this in a gettext.bbclass patch
def gettext_oemeson(d):
    if d.getVar('USE_NLS') == 'no':
        return '-Dnls=disabled'
    # Remove the NLS bits if USE_NLS is no or INHIBIT_DEFAULT_DEPS is set
    if d.getVar('INHIBIT_DEFAULT_DEPS') and not oe.utils.inherits(d, 'cross-canadian'):
        return '-Dnls=disabled'
    return '-Dnls=enabled'

EXTRA_OEMESON += " \
    -Ddoc=disabled \
    -Dexamples=disabled \
    -Ddbghelp=disabled \
    ${@gettext_oemeson(d)} \
"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"

PACKAGES += "${PN}-bash-completion"

# Add the core element plugins to the main package
FILES:${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES:${PN}-dev += "${libdir}/gstreamer-1.0/*.a ${libdir}/gstreamer-1.0/include"
FILES:${PN}-bash-completion += "${datadir}/bash-completion/completions/ ${datadir}/bash-completion/helpers/gst*"
FILES:${PN}-dbg += "${datadir}/gdb ${datadir}/gstreamer-1.0/gdb"

RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-gconv-iso8859-5"

CVE_PRODUCT = "gstreamer"

CVE_STATUS[CVE-2024-0444] = "cpe-incorrect: this is patched in gstreamer1.0-plugins-bad in 1.22 branch since 1.22.9"

CVE_STATUS_GROUPS += "CVE_STATUS_PLUGINS_BAD"
CVE_STATUS_PLUGINS_BAD = " \
    CVE-2025-3887 \
"
CVE_STATUS_PLUGINS_BAD[status] = "cpe-incorrect: this is patched in gstreamer1.0-plugins-bad"

CVE_STATUS_GROUPS += "CVE_STATUS_PLUGINS_BASE"
CVE_STATUS_PLUGINS_BASE = " \
    CVE-2024-47538 CVE-2024-47541 CVE-2024-47542 CVE-2024-47600 CVE-2024-47607 CVE-2024-47615 CVE-2024-47835 \
    CVE-2025-47806 CVE-2025-47807 CVE-2025-47808 \
"
CVE_STATUS_PLUGINS_BASE[status] = "cpe-incorrect: this is patched in gstreamer1.0-plugins-base"

CVE_STATUS_GROUPS += "CVE_STATUS_PLUGINS_GOOD"
CVE_STATUS_PLUGINS_GOOD = " \
    CVE-2024-47537 CVE-2024-47539 CVE-2024-47540 CVE-2024-47543 CVE-2024-47544 CVE-2024-47545 \
    CVE-2024-47546 CVE-2024-47596 CVE-2024-47597 CVE-2024-47598 CVE-2024-47599 CVE-2024-47601 \
    CVE-2024-47602 CVE-2024-47603 CVE-2024-47613 CVE-2024-47774 CVE-2024-47775 CVE-2024-47776 \
    CVE-2024-47777 CVE-2024-47778 CVE-2024-47834 CVE-2025-47183 CVE-2025-47219 \
"
CVE_STATUS_PLUGINS_GOOD[status] = "cpe-incorrect: this is patched in gstreamer1.0-plugins-good"

CVE_STATUS[CVE-2025-2759] = "not-applicable-platform: affects installation packages for non Linux OSes"

PTEST_BUILD_HOST_FILES = ""
