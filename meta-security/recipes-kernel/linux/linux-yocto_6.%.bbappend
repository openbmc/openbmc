require ${@bb.utils.contains('DISTRO_FEATURES', 'security', '${BPN}_security.inc', '', d)}
