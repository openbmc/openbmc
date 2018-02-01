# Inherit this class to enable or disable building and installation of manpages
# depending on whether 'api-documentation' is in DISTRO_FEATURES. Such building
# tends to pull in the entire XML stack and other tools, so it's not enabled
# by default.
PACKAGECONFIG_append_class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'manpages', '', d)}"
