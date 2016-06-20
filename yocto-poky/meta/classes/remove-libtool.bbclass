# This class removes libtool .la files after do_install

REMOVE_LIBTOOL_LA ?= "1"

remove_libtool_la() {
	if [ "${REMOVE_LIBTOOL_LA}" != "0" ]; then
		find "${D}" -ignore_readdir_race -name "*.la" -delete
	fi
}

do_install[postfuncs] += "remove_libtool_la"
