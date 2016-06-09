FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://libev-config-guess.patch"
SRC_URI += "file://0001-gevent-py279-ssl-wrap.patch"

