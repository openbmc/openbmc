# This feature enables settings that may be more appropriate for production
# or deployment environments than development.

DISTRO_FEATURES += "phosphor-production"
DISTROOVERRIDES .= ":df-phosphor-production"
