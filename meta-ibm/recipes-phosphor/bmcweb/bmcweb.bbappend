SUMMARY = "Modifications to support Nginx"

# Behind NGINX so disable SSL in bmcweb
EXTRA_OECMAKE += "-DBMCWEB_INSECURE_DISABLE_SSL=ON"

# Only need redfish support
EXTRA_OECMAKE += "-DBMCWEB_ENABLE_KVM=OFF -DBMCWEB_ENABLE_DBUS_REST=OFF -DBMCWEB_ENABLE_STATIC_HOSTING=OFF"

# override service and socket file to use nginx
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
