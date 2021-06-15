require libmodbus.inc

SRC_URI += "file://f1eb4bc7ccb09cd8d19ab641ee37637f8c34d16d.patch \
	    file://Fix-float-endianness-issue-on-big-endian-arch.patch \
	    file://Fix-typo.patch"
SRC_URI[md5sum] = "15c84c1f7fb49502b3efaaa668cfd25e"
SRC_URI[sha256sum] = "d7d9fa94a16edb094e5fdf5d87ae17a0dc3f3e3d687fead81835d9572cf87c16"

# this file has been created one minute after the configure file, so it doesn't get recreated during configure step
do_configure_prepend() {
	rm -rf ${S}/tests/unit-test.h
}
