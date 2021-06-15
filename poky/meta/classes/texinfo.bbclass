# This class is inherited by recipes whose upstream packages invoke the
# texinfo utilities at build-time. Native and cross recipes are made to use the
# dummy scripts provided by texinfo-dummy-native, for improved performance.
# Target architecture recipes use the genuine Texinfo utilities. By default,
# they use the Texinfo utilities on the host system. If you want to use the
# Texinfo recipe, you can remove texinfo-native from ASSUME_PROVIDED and
# makeinfo from SANITY_REQUIRED_UTILITIES.

TEXDEP = "${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'texinfo-replacement-native', 'texinfo-dummy-native', d)}"
TEXDEP_class-native = "texinfo-dummy-native"
TEXDEP_class-cross = "texinfo-dummy-native"
TEXDEP_class-crosssdk = "texinfo-dummy-native"
TEXDEP_class-cross-canadian = "texinfo-dummy-native"
DEPENDS_append = " ${TEXDEP}"

# libtool-cross doesn't inherit cross
TEXDEP_pn-libtool-cross = "texinfo-dummy-native"

