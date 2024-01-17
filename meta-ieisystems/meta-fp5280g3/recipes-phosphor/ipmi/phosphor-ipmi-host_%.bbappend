EXTRA_OEMESON:append = " \
        -Dget-dbus-active-software=enabled \
        -Dfw-ver-regex="([\\\\d]+).([\\\\d]+).([\\\\d]+)-dev-([\\\\d]+)-g([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})" \
        -Dmatches-map="1,2,5,6,7,8" \
        "

