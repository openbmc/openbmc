# gi-docgen is a new gnome documentation generator, which
# seems to be a successor to gtk-doc:
# https://gitlab.gnome.org/GNOME/gi-docgen

# This variable is set to True if api-documentation is in
# DISTRO_FEATURES, and False otherwise.
GIDOCGEN_ENABLED ?= "${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'True', 'False', d)}"
# When building native recipes, disable gi-docgen, as it is not necessary,
# pulls in additional dependencies, and makes build times longer
GIDOCGEN_ENABLED:class-native = "False"
GIDOCGEN_ENABLED:class-nativesdk = "False"

# meson: default option name to enable/disable gi-docgen. This matches most
# projects' configuration. In doubts - check meson_options.txt in project's
# source path.
GIDOCGEN_MESON_OPTION ?= 'gtk_doc'
GIDOCGEN_MESON_ENABLE_FLAG ?= 'true'
GIDOCGEN_MESON_DISABLE_FLAG ?= 'false'

# Auto enable/disable based on GIDOCGEN_ENABLED
EXTRA_OEMESON:prepend = "-D${GIDOCGEN_MESON_OPTION}=${@bb.utils.contains('GIDOCGEN_ENABLED', 'True', '${GIDOCGEN_MESON_ENABLE_FLAG}', '${GIDOCGEN_MESON_DISABLE_FLAG}', d)} "

DEPENDS:append = "${@' gi-docgen-native gi-docgen' if d.getVar('GIDOCGEN_ENABLED') == 'True' else ''}"

