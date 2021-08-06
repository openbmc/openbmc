# Prevent lmsensors from pulling in lighttpd as lighttpd
# uses md4 and we disable openssl md4 support.

RRECOMMENDS:${PN}-cgi:remove = "lighttpd lighttpd-module-cgi"
