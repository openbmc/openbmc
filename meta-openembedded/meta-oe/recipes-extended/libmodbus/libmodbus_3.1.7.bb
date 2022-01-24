require libmodbus.inc

SRC_URI += "file://Fix-float-endianness-issue-on-big-endian-arch.patch"
SRC_URI[sha256sum] = "7dfe958431d0570b271e1a5b329b76a658e89c614cf119eb5aadb725c87f8fbd"

# this file has been created one minute after the configure file, so it doesn't get recreated during configure step
do_configure:prepend() {
	rm -rf ${S}/tests/unit-test.h
}
