-- These features require a session DBus instance, which is not available
-- by default in OE generated images. The absence of such a DBus instance
-- causes WirePlumber to fail to start. Turn these off to prevent that.
alsa_monitor.properties["alsa.reserve"] = false
default_access.properties["enable-flatpak-portal"] = false
