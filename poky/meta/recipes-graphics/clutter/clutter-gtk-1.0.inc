SUMMARY = "Library for embedding a Clutter canvas in a GTK+ application"
HOMEPAGE = "http://www.clutter-project.org/"
LICENSE = "LGPLv2+"

CLUTTERBASEBUILDCLASS = "meson"
inherit clutter distro_features_check upstream-version-is-even gobject-introspection gtk-doc

# depends on clutter-1.0 which depends on cogl-1.0
REQUIRED_DISTRO_FEATURES ?= "opengl"
# depends on gtk+3
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS = "clutter-1.0 gtk+3"
PACKAGES  =+ "${PN}-examples"

GTKDOC_ENABLE_FLAG = "-Denable_docs=true"
GTKDOC_DISABLE_FLAG = "-Denable_docs=false"

GI_ENABLE_FLAG = "-Denable_gi=true"
GI_DISABLE_FLAG = "-Denable_gi=false"

EXTRA_OEMESON_append_class-nativesdk = " ${GI_DISABLE_FLAG}"

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '${GI_ENABLE_FLAG}', \
                                                                                       '${GI_DISABLE_FLAG}', d)} "

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GTKDOC_ENABLED', 'True', '${GTKDOC_ENABLE_FLAG}', \
                                                                                     '${GTKDOC_DISABLE_FLAG}', d)} "



