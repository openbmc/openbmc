require vala-dbus-binding-tool.inc

SRC_URI[md5sum] = "59eab7abf38f35355d3786803bd2441f"
SRC_URI[sha256sum] = "1e37ab2e6238eaef9f573560ea7379e6955570f7c9503083e50c4c185c1956df"

PNBLACKLIST[vala-dbus-binding-tool] ?= "Fails to build with RSS http://errors.yoctoproject.org/Errors/Details/131628/ - the recipe will be removed on 2017-09-01 unless the issue is fixed"
