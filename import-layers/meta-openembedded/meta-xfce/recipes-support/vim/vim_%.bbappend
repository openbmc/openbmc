require ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${BPN}_xfce.inc', '', d)}
