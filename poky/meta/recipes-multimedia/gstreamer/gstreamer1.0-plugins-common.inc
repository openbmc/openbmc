# This .inc file contains the common setup for the gstreamer1.0-plugins-*
# plugin set recipes.

# SUMMARY is set in the actual .bb recipes
HOMEPAGE = "https://gstreamer.freedesktop.org/"
BUGTRACKER = "https://bugzilla.gnome.org/enter_bug.cgi?product=Gstreamer"
SECTION = "multimedia"

DEPENDS = "gstreamer1.0 glib-2.0-native"

inherit gettext meson pkgconfig upstream-version-is-even

require gstreamer1.0-plugins-packaging.inc

# Orc enables runtime JIT compilation of data processing routines from Orc
# bytecode to SIMD instructions for various architectures (currently SSE, MMX,
# MIPS, Altivec and NEON are supported).
# This value is used in the PACKAGECONFIG values for each plugin set recipe.
# By modifying it, Orc can be enabled/disabled in all of these recipes at once.
GSTREAMER_ORC ?= "orc"
# workaround to disable orc on mips to fix the build failure
# {standard input}: Assembler messages:
# {standard input}:46587: Error: branch out of range
GSTREAMER_ORC:mips = ""
PACKAGECONFIG[orc] = "-Dorc=enabled,-Dorc=disabled,orc orc-native"

# TODO: put this in a gettext.bbclass patch (with variables to allow for
# configuring the option name and the enabled/disabled values).
def gettext_oemeson(d):
    if d.getVar('USE_NLS') == 'no':
        return '-Dnls=disabled'
    # Remove the NLS bits if USE_NLS is no or INHIBIT_DEFAULT_DEPS is set
    if d.getVar('INHIBIT_DEFAULT_DEPS') and not oe.utils.inherits(d, 'cross-canadian'):
        return '-Dnls=disabled'
    return '-Dnls=enabled'

# Not all plugin sets contain examples, so the -Dexamples
# option needs to be added conditionally.
GST_PLUGIN_SET_HAS_EXAMPLES ?= "1"

EXTRA_OEMESON += " \
    ${@bb.utils.contains('GST_PLUGIN_SET_HAS_EXAMPLES', '1', '-Dexamples=disabled', '', d)} \
    ${@gettext_oemeson(d)} \
"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"
