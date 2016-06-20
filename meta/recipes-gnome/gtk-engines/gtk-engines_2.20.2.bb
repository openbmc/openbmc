SUMMARY = "GTK+ theme engines"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

SECTION = "x11/base"
DEPENDS = "intltool-native gtk+ gettext-native"

PR = "r3"

PACKAGES += "${PN}-schemas"
PACKAGES_DYNAMIC += "^gtk-engine-.* ^gtk-theme-.*"

RDEPENDS_gtk-theme-redmond = "gtk-engine-redmond95"
RDEPENDS_gtk-theme-metal = "gtk-engine-metal"
RDEPENDS_gtk-theme-mist = "gtk-engine-mist"
RDEPENDS_gtk-theme-crux = "gtk-engine-crux-engine"
RDEPENDS_gtk-theme-lighthouseblue = "gtk-engine-lighthouseblue"
RDEPENDS_gtk-theme-thinice = "gtk-engine-thinice"
RDEPENDS_gtk-theme-industrial = "gtk-engine-industrial"
RDEPENDS_gtk-theme-clearlooks = "gtk-engine-clearlooks"

FILES_${PN} = ""
FILES_${PN}-dev += "${libdir}/gtk-2.0/*/engines/*.la"
FILES_${PN}-schemas = "${datadir}/gtk-engines/*.xml"

CFLAGS_prepend = "-DHAVE_ANIMATION "

RDEPENDS_${PN}-dev = ""

inherit gnomebase
GNOME_COMPRESS_TYPE="bz2"

inherit distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

# This picks stable releases in the 2.x series (but not 2.90 onwards,
# which were GNOME 3 betas).
UPSTREAM_CHECK_REGEX = "(?P<pver>2\.([0-8]*[02468])+(\.\d+)+)"

python populate_packages_prepend() {
    engines_root = os.path.join(d.getVar('libdir', True), "gtk-2.0/2.10.0/engines")
    themes_root = os.path.join(d.getVar('datadir', True), "themes")

    do_split_packages(d, engines_root, '^lib(.*)\.so$', 'gtk-engine-%s', 'GTK %s theme engine', extra_depends='')
    do_split_packages(d, themes_root, '(.*)', 'gtk-theme-%s', 'GTK theme %s', allow_dirs=True, extra_depends='')
    # TODO: mark theme packages as arch all
}

SRC_URI += "file://glib-2.32.patch \
            file://substitute-tests.patch"
SRC_URI[archive.md5sum] = "5deb287bc6075dc21812130604c7dc4f"
SRC_URI[archive.sha256sum] = "15b680abca6c773ecb85253521fa100dd3b8549befeecc7595b10209d62d66b5"
