# Main recipe was moved to oe-core, but with ptest disabled
inherit ${@bb.utils.filter('DISTRO_FEATURES', 'ptest', d)}
